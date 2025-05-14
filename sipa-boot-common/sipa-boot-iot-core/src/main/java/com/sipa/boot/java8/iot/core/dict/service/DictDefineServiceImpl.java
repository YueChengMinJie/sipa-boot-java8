package com.sipa.boot.java8.iot.core.dict.service;

import static com.sipa.boot.java8.common.constants.SipaBootCommonConstants.ACROSS;
import static com.sipa.boot.java8.common.constants.SipaBootCommonConstants.UNDERLINE;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.sipa.boot.java8.common.log.Log;
import com.sipa.boot.java8.common.log.LogFactory;
import com.sipa.boot.java8.common.utils.StringUtils;
import com.sipa.boot.java8.iot.core.dict.DefaultDictDefine;
import com.sipa.boot.java8.iot.core.dict.DefaultItemDefine;
import com.sipa.boot.java8.iot.core.dict.base.Dict;
import com.sipa.boot.java8.iot.core.dict.base.IDictDefine;
import com.sipa.boot.java8.iot.core.dict.base.IEnumDict;
import com.sipa.boot.java8.iot.core.dict.service.base.IDictDefineService;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * @author caszhou
 * @date 2021/10/5
 */
@Service
public class DictDefineServiceImpl implements IDictDefineService {
    private static final Log log = LogFactory.get(DictDefineServiceImpl.class);

    protected static final Map<String, IDictDefine> PARSED_DICT = new HashMap<>();

    public static void registerDefine(IDictDefine define) {
        if (define == null) {
            return;
        }
        PARSED_DICT.put(define.getId(), define);
    }

    @SuppressWarnings("rawtypes")
    public static IDictDefine parseEnumDict(Class<?> type) {
        if (!type.isEnum()) {
            throw new UnsupportedOperationException("Unsupported type [" + type + "]");
        }

        Dict dict = type.getAnnotation(Dict.class);
        List<IEnumDict<?>> items = new ArrayList<>();
        for (Object enumConstant : type.getEnumConstants()) {
            if (enumConstant instanceof IEnumDict) {
                items.add((IEnumDict)enumConstant);
            } else {
                Enum e = ((Enum)enumConstant);
                items.add(DefaultItemDefine.DefaultItemDefineBuilder.aDefaultItemDefine()
                    .withValue(e.name())
                    .withText(e.name())
                    .withOrdinal(e.ordinal())
                    .build());
            }
        }

        DefaultDictDefine define = new DefaultDictDefine();
        if (dict != null) {
            define.setId(dict.value());
            define.setComments(dict.comments());
            define.setAlias(dict.alias());
        } else {
            String id = StringUtils.camelCase2UnderScoreCase(type.getSimpleName()).replace(UNDERLINE, ACROSS);
            if (id.startsWith(ACROSS)) {
                id = id.substring(1);
            }
            define.setId(id);
            define.setAlias(type.getSimpleName());
        }
        define.setItems(items);
        log.trace("Parse enum dict [{}] as [{}]", type, define.getId());
        return define;
    }

    @Override
    public Mono<IDictDefine> getDefine(String id) {
        return Mono.justOrEmpty(PARSED_DICT.get(id));
    }

    @Override
    public Flux<IDictDefine> getAllDefine() {
        return Flux.fromIterable(PARSED_DICT.values());
    }

    @Override
    public void addDefine(IDictDefine dictDefine) {
        registerDefine(dictDefine);
    }
}
