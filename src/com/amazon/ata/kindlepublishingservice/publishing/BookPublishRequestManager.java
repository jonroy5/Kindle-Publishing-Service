package com.amazon.ata.kindlepublishingservice.publishing;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class BookPublishRequestManager {

    LinkedList<BookPublishRequest> publishRequestList = new LinkedList<>();
    private BookPublishRequest bookPublishRequest;

    @Inject
    public BookPublishRequestManager() {

        publishRequestList = new LinkedList<>();

    }

    public void addBookPublishRequest(BookPublishRequest bookPublishRequest) {

        publishRequestList.add(bookPublishRequest);

    }

    public BookPublishRequest getBookPublishRequestToProcess() {

       BookPublishRequest request = publishRequestList.poll();
        return request;
    }

}
