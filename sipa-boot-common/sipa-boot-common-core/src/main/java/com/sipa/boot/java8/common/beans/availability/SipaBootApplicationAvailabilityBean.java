package com.sipa.boot.java8.common.beans.availability;

import org.springframework.boot.availability.*;

/**
 * @author zhouxiajie
 * @date 2021/3/24
 */
public class SipaBootApplicationAvailabilityBean extends ApplicationAvailabilityBean {
    @Override
    public <S extends AvailabilityState> S getState(Class<S> stateType) {
        AvailabilityChangeEvent<S> event = getLastChangeEvent(stateType);
        return (event != null) ? event.getState() : getDefaultState(stateType);
    }

    @SuppressWarnings("unchecked")
    private <S extends AvailabilityState> S getDefaultState(Class<S> stateType) {
        if (stateType == LivenessState.class) {
            return (S)LivenessState.BROKEN;
        } else if (stateType == ReadinessState.class) {
            return (S)ReadinessState.ACCEPTING_TRAFFIC;
        }
        return null;
    }
}
