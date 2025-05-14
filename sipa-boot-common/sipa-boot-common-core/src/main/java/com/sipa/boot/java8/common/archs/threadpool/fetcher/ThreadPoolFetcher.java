package com.sipa.boot.java8.common.archs.threadpool.fetcher;

import java.util.*;
import java.util.concurrent.*;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.IterableUtils;
import org.apache.commons.collections4.MapUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Lists;
import com.sipa.boot.java8.common.archs.threadpool.common.ThreadPoolConstants;
import com.sipa.boot.java8.common.archs.threadpool.exception.ThreadPoolProcessException;
import com.sipa.boot.java8.common.archs.threadpool.exception.ThreadPoolResultEmptyException;
import com.sipa.boot.java8.common.archs.threadpool.exception.ThreadPoolTimeoutException;
import com.sipa.boot.java8.common.archs.threadpool.pojo.ThreadPoolProcess;
import com.sipa.boot.java8.common.archs.threadpool.pojo.ThreadPoolProcessMetadata;
import com.sipa.boot.java8.common.archs.threadpool.pojo.ThreadPoolResult;
import com.sipa.boot.java8.common.archs.threadpool.pojo.Tuple2;

/**
 * @author sunyukun
 * @since 2019/8/6 15:02
 */
public class ThreadPoolFetcher {
    private static final Logger LOGGER = LoggerFactory.getLogger(ThreadPoolFetcher.class);

    private ThreadPoolFetcher() {}

    private static class ThreadPoolFetcherHolder {
        private static final ThreadPoolFetcher INSTANCE = new ThreadPoolFetcher();
    }

    public static ThreadPoolFetcher getInstance() {
        return ThreadPoolFetcherHolder.INSTANCE;
    }

    /**
     * Supply async task list with Function to the specified thread pool.
     *
     * @param threadPool
     *            thread pool
     * @param function
     *            the task function
     * @param metadataFunction
     *            the metadata function
     * @param tppm
     *            the metadata wrapper
     * @param iterable
     *            the input parameter list of task function
     * @param batchSizeLimit
     *            the partition size
     * @param <T>
     *            the input type of task function
     * @param <R>
     *            the return type of task function
     * @return Tuple2 object with result List and completable future array.
     */
    public <T, R> List<Tuple2<ThreadPoolResult<R>, CompletableFuture<Void>>> supplyAsyncTaskListWithFunction(
        Executor threadPool, Function<ThreadPoolProcess<T>, R> function,
        Function<ThreadPoolProcessMetadata, Map<String, String>> metadataFunction, ThreadPoolProcessMetadata tppm,
        Iterable<T> iterable, final int batchSizeLimit) throws ThreadPoolResultEmptyException {
        if (threadPool == null) {
            throw new ThreadPoolProcessException(
                "[ThreadPoolFetcher] - [supplyAsyncTaskListWithFunction] : Thread Pool is null.");
        }

        List<T> itemList = IterableUtils.toList(iterable);
        if (CollectionUtils.isEmpty(itemList)) {
            throw new ThreadPoolResultEmptyException();
        } else {
            List<List<T>> partList = Lists.partition(itemList, batchSizeLimit);
            LOGGER.info(
                "[ThreadPoolFetcher] - [supplyAsyncTaskListWithFunction] - Task list with size {} will be split to {} sub lists.",
                itemList.size(), partList.size());
            int[] index = {0};
            return partList.stream()
                .map(subItemList -> supplyAsyncTaskListWithFunction(threadPool, function, metadataFunction, tppm,
                    index[0]++, batchSizeLimit, subItemList))
                .collect(Collectors.toList());
        }
    }

    /**
     * Supply async task list with Function to the specified thread pool.
     *
     * @param threadPool
     *            thread pool
     * @param function
     *            the task function
     * @param metadataFunction
     *            the metadata function
     * @param tppm
     *            the metadata wrapper
     * @param index
     *            this partition index
     * @param partitionSize
     *            this partition size
     * @param iterable
     *            the input parameter list of task function
     * @param <T>
     *            the input type of task function
     * @param <R>
     *            the return type of task function
     * @return Tuple2 object with result List and completable future array.
     */
    private <T, R> Tuple2<ThreadPoolResult<R>, CompletableFuture<Void>> supplyAsyncTaskListWithFunction(
        Executor threadPool, Function<ThreadPoolProcess<T>, R> function,
        Function<ThreadPoolProcessMetadata, Map<String, String>> metadataFunction, ThreadPoolProcessMetadata tppm,
        int index, int partitionSize, Iterable<T> iterable)
        throws ThreadPoolResultEmptyException, ThreadPoolProcessException {
        if (threadPool == null) {
            throw new ThreadPoolProcessException(
                "[ThreadPoolFetcher] - [supplyAsyncTaskListWithFunction] : Thread Pool is null.");
        }

        List<T> items = IterableUtils.toList(iterable);
        if (CollectionUtils.isEmpty(items)) {
            throw new ThreadPoolResultEmptyException();
        } else {
            ThreadPoolProcess<T> tpp = getTpp(items, index, partitionSize, metadataFunction, tppm);
            ThreadPoolResult<R> result =
                ThreadPoolResult.ThreadPoolResultBuilder.<R>aThreadPoolResult().withMetadata(tpp.getMetadata()).build();
            return new Tuple2<>(result,
                CompletableFuture.supplyAsync(() -> function.apply(tpp), threadPool).thenAccept(result::setData));
        }
    }

    private <T> ThreadPoolProcess<T> getTpp(List<T> items, int index, int partitionSize,
        Function<ThreadPoolProcessMetadata, Map<String, String>> metadataFunction, ThreadPoolProcessMetadata tppm) {
        ThreadPoolProcess<T> tpp = ThreadPoolProcess.ThreadPoolProcessBuilder.<T>aThreadPoolProcess()
            .withItems(items)
            .withIndex(index)
            .withPartitionSize(partitionSize)
            .build();
        if (Objects.nonNull(metadataFunction) && Objects.nonNull(tppm)) {
            setTpp2Tppm(tpp, tppm);
            tpp.setMetadata(metadataFunction.apply(tppm));
        }
        return tpp;
    }

    private <T> void setTpp2Tppm(ThreadPoolProcess<T> tpp, ThreadPoolProcessMetadata tppm) {
        Map<String, Object> tppmMap = tppm.getData();
        if (MapUtils.isNotEmpty(tppmMap)) {
            // 可以添加更多你需要的数据
            tppmMap.put(ThreadPoolConstants.INDEX_KEY, tpp.getIndex());
            tppmMap.put(ThreadPoolConstants.PARTITION_SIZE_KEY, tpp.getPartitionSize());
            tppmMap.put(ThreadPoolConstants.SIZE_KEY, tpp.getItems().size());
        }
    }

    /**
     * Get the result of task list from CompletableFuture Stream by specified timeout.
     *
     * @param taskName
     *            task name
     * @param timeoutInSeconds
     *            timeout when get data by a future object.
     */
    public void collectResultAndHandleException(CompletableFuture<?>[] futureArray, String taskName,
        long timeoutInSeconds) {
        if (timeoutInSeconds == -1) {
            LOGGER.info("Timeout is be disabled due to timeoutInSeconds [{}], wait until all sub task finish.",
                timeoutInSeconds);
            CompletableFuture.allOf(futureArray).exceptionally(e -> {
                LOGGER.error("[ThreadPoolFetcher] - [getFutureResultList] - [Error]", e);
                throw new ThreadPoolProcessException(
                    "[ThreadPoolFetcher] - [collectResultAndHandleException] - [" + taskName + "] : " + e.getMessage());
            }).join();
        } else {
            try {
                CompletableFuture.allOf(futureArray).get(timeoutInSeconds, TimeUnit.SECONDS);
            } catch (InterruptedException | ExecutionException e) {
                LOGGER.error("[ThreadPoolFetcher] - [collectResultAndHandleException] - [Error]", e);
                throw new ThreadPoolProcessException(
                    "[ThreadPoolFetcher] - [collectResultAndHandleException] - [" + taskName + "] : " + e.getMessage());
            } catch (TimeoutException e) {
                LOGGER.error("[ThreadPoolFetcher] - [collectResultAndHandleException] - [" + taskName + "] - [Error]",
                    e);
                throw new ThreadPoolTimeoutException("[ThreadPoolFetcher] - [collectResultAndHandleException] - ["
                    + taskName + "] is timeout in " + timeoutInSeconds + " seconds.");
            }
        }
    }

    /**
     * Supply async task without a parameter to the specified thread pool.
     *
     * @param threadPool
     *            thread pool
     * @param supplier
     *            task supplier
     * @param <R>
     *            the return type of task function
     * @return Completable Future object with specified type.
     */
    public <R> Tuple2<List<R>, CompletableFuture<Void>> supplyAsyncTaskWithSupplier(Executor threadPool,
        Supplier<R> supplier) {
        if (threadPool == null) {
            throw new ThreadPoolProcessException(
                "[ThreadPoolFetcher] - [supplyAsyncTaskWithSupplier] : Thread Pool is null.");
        }
        final List<R> resultList = Collections.synchronizedList(new ArrayList<>());
        return new Tuple2<>(resultList, supplyAsyncTask(threadPool, supplier).thenAccept(resultList::add));
    }

    /**
     * Supply async task with wrapper supplier.
     *
     * @param threadPool
     *            thread pool
     * @param supplier
     *            task supplier
     * @param <R>
     *            the return type of task function.
     * @return Completable feature object with specified type.
     */
    private <R> CompletableFuture<R> supplyAsyncTask(Executor threadPool, Supplier<R> supplier)
        throws ThreadPoolProcessException {
        if (threadPool == null) {
            throw new ThreadPoolProcessException(
                "[ThreadPoolFetcher] - [supplyAsyncTaskWithSupplier] : Thread Pool is null.");
        }
        return CompletableFuture.supplyAsync(supplier, threadPool);
    }
}
