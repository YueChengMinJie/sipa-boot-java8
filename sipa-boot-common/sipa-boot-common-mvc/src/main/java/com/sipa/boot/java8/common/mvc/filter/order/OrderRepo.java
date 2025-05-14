package com.sipa.boot.java8.common.mvc.filter.order;

import org.springframework.core.Ordered;

/**
 * @author feizhihao
 * @date 2019-05-08
 */
public final class OrderRepo {
    /**
     * from the highest order to the lowest order
     */
    private static final int NEGATIVE_1000 = -1000;

    private static final int NEGATIVE_950 = -950;

    private static final int NEGATIVE_900 = -900;

    private static final int NEGATIVE_870 = -870;

    private static final int NEGATIVE_860 = -860;

    private static final int NEGATIVE_850 = -850;

    private static final int NEGATIVE_800 = -800;

    private static final int NEGATIVE_750 = -750;

    private static final int NEGATIVE_700 = -700;

    private static final int NEGATIVE_650 = -650;

    private static final int NEGATIVE_600 = -600;

    private static final int NEGATIVE_550 = -550;

    /**
     * base
     */
    private static final int REQUEST_ID_FILTER_ORDER = NEGATIVE_1000;

    private static final int USER_ID_FILTER_ORDER = NEGATIVE_950;

    private static final int TENANT_ID_FILTER_ORDER = NEGATIVE_900;

    private static final int SCOPE_FILTER_ORDER = NEGATIVE_870;

    private static final int AUTHORITIES_FILTER_ORDER = NEGATIVE_860;

    private static final int DEVICE_ID_FILTER_ORDER = NEGATIVE_850;

    private static final int CLIENT_ID_FILTER_ORDER = NEGATIVE_800;

    private static final int REQUEST_FROM_FILTER_ORDER = NEGATIVE_750;

    private static final int USER_AGENT_FILTER_ORDER = NEGATIVE_700;

    private static final int REQUEST_IP_FILTER_ORDER = NEGATIVE_600;

    private static final int KEEP_ALIVE_FILTER_ORDER = NEGATIVE_550;

    /**
     * common
     */
    private static final int LOGGING_FILTER_ORDER = NEGATIVE_650;

    /**
     * for base use
     */
    public enum BaseReqFilter implements Ordered {
        RequestId {
            @Override
            public int getOrder() {
                return REQUEST_ID_FILTER_ORDER;
            }
        },
        UserId {
            @Override
            public int getOrder() {
                return USER_ID_FILTER_ORDER;
            }
        },
        TenantId {
            @Override
            public int getOrder() {
                return TENANT_ID_FILTER_ORDER;
            }
        },
        Scope {
            @Override
            public int getOrder() {
                return SCOPE_FILTER_ORDER;
            }
        },
        Authorities {
            @Override
            public int getOrder() {
                return AUTHORITIES_FILTER_ORDER;
            }
        },
        DeviceId {
            @Override
            public int getOrder() {
                return DEVICE_ID_FILTER_ORDER;
            }
        },
        ClientId {
            @Override
            public int getOrder() {
                return CLIENT_ID_FILTER_ORDER;
            }
        },
        RequestFrom {
            @Override
            public int getOrder() {
                return REQUEST_FROM_FILTER_ORDER;
            }
        },
        UserAgent {
            @Override
            public int getOrder() {
                return USER_AGENT_FILTER_ORDER;
            }
        },
        RequestIp {
            @Override
            public int getOrder() {
                return REQUEST_IP_FILTER_ORDER;
            }
        }
    }

    /**
     * for common shared
     */
    public enum CommonReqFilter implements Ordered {
        Logging {
            @Override
            public int getOrder() {
                return LOGGING_FILTER_ORDER;
            }
        };
    }

    /**
     * for header use
     */
    public enum HeaderReqFilter implements Ordered {
        KEEP_ALIVE {
            @Override
            public int getOrder() {
                return KEEP_ALIVE_FILTER_ORDER;
            }
        }
    }
}
