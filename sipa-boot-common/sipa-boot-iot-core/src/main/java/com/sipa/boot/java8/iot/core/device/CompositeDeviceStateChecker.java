package com.sipa.boot.java8.iot.core.device;

import java.util.Comparator;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import javax.validation.constraints.NotNull;

import com.sipa.boot.java8.iot.core.device.base.IDeviceOperator;
import com.sipa.boot.java8.iot.core.device.base.IDeviceStateChecker;

import reactor.core.publisher.Mono;

/**
 * @author caszhou
 * @date 2021/10/2
 */
public class CompositeDeviceStateChecker implements IDeviceStateChecker {
    private final List<IDeviceStateChecker> checkerList = new CopyOnWriteArrayList<>();

    public void addDeviceStateChecker(IDeviceStateChecker checker) {
        checkerList.add(checker);
        checkerList.sort(Comparator.comparing(IDeviceStateChecker::order));
    }

    @Override
    public @NotNull Mono<Byte> checkState(@NotNull IDeviceOperator device) {
        if (checkerList.isEmpty()) {
            return Mono.empty();
        }
        if (checkerList.size() == 1) {
            return checkerList.get(0).checkState(device);
        }
        Mono<Byte> checker = checkerList.get(0).checkState(device);
        for (int i = 1, len = checkerList.size(); i < len; i++) {
            checker = checker.switchIfEmpty(checkerList.get(i).checkState(device));
        }
        return checker;
    }
}
