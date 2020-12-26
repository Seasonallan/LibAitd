package com.library.aitd.bean;

import com.library.aitd.bean.base.Json;

import java.io.Serializable;

/**
 "ledger_current_index": 22,
 "status": "success"
 */
public class XRPLedger extends BaseResult implements Serializable {
    @Json(name = "")
    public String ledger_current_index;
}
