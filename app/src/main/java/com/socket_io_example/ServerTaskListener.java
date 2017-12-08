package com.socket_io_example;

public interface ServerTaskListener {

    void onTaskSuccess(Object object);

    void onTaskFailure(String message);
}
