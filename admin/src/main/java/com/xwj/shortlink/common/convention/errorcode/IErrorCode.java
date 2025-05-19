package com.xwj.shortlink.common.convention.errorcode;

/**
 * 平台错误码
 */
public interface IErrorCode {
    /**
     * 错误码
     * @return
     */
    String code();

    /**
     * 错误信息
     * @return
     */
    String message();
}
