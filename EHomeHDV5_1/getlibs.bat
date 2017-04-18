IF "%1" EQU "" (
    set dest=.\eHomeHD\libs\wl_sdk_wan.jar
) else (
    set dest=%1
)
curl -o %dest% http://172.18.0.237/sdkdownload/ClientSDKJava/V5.3.2-20160926-12/wl_sdk_wan.jar
