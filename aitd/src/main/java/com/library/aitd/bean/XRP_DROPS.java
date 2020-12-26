package com.library.aitd.bean;

import com.library.aitd.bean.base.BaseDao;
import com.library.aitd.bean.base.Json;

import java.io.Serializable;

/**
 * "base_fee": "10",
 * "median_fee": "5000",
 * "minimum_fee": "10",
 * "open_ledger_fee": "10"
 */
public class XRP_DROPS extends BaseDao implements Serializable {
    @Json(name = "")
    public String base_fee;
    @Json(name = "")
    public String median_fee;
    @Json(name = "")
    public String minimum_fee;
    @Json(name = "")
    public String open_ledger_fee;
}
