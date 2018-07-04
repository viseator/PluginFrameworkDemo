// IRemoteService.aidl
package com.viseator.remote_service;

// Declare any non-default types here with import statements

interface IRemoteService {
    /**
     * Demonstrates some basic types that you can use as parameters
     * and return values in AIDL.
     */
     String requestStringFromService(int requestCode);
}
