package com.tutk.IOTC.monitor;

import com.yuantuo.netsdk.TKCamHelper;

public interface I_MonitorExternalSetup {
      public void attachCamera(TKCamHelper camera, int avChannel);
      public void deattachCamera();
}
