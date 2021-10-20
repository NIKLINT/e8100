package com.escom.talkapp.ui;

import com.impl.struct.RF_CallStatusUpdate;

/**************************************
 * define call status Update callback interface
 */
public interface ITSCallInfoUpdate {
    void OnCallInfoUpdate(RF_CallStatusUpdate CallInfo);
    void OnCallGroupIndexUpdate(short GroupIndex);
    void OnResetCallTo(String callId, byte callType, byte emergency);
    void OnDirecttalkCreate();
    void OnDirecttalkDestroy();
}
