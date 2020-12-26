package com.library.aitd.bean.transaction;

import com.library.aitd.bean.base.BaseDao;
import com.library.aitd.bean.base.Json;

import java.io.Serializable;

/**
 *
 */
public class XRPMarker extends BaseDao implements Serializable {
    @Json(name = "")
    public int ledger;
    @Json(name = "")
    public int seq;
}
