package com.amazon.ata.kindlepublishingservice.converters;

import com.amazon.ata.kindlepublishingservice.dynamodb.models.PublishingStatusItem;
import com.amazon.ata.kindlepublishingservice.enums.PublishingRecordStatus;
import com.amazon.ata.kindlepublishingservice.models.PublishingStatusRecord;

import java.util.*;

public class PublishingStatusRecordConverter {

    private PublishingStatusItem publishingStatusItem;

    public PublishingStatusRecord toPublishingStatusRecord(PublishingStatusItem publishingStatusItem) {
        PublishingRecordStatus status = publishingStatusItem.getStatus();
        String bookId = publishingStatusItem.getBookId();
        String message = publishingStatusItem.getStatusMessage();

       return PublishingStatusRecord.builder()
                .withStatus(status.toString())
                .withStatusMessage(message)
                .withBookId(bookId)
                .build();
    }

    public List<PublishingStatusRecord> toPublishingStatusRecordList(List<PublishingStatusItem> itemList) {
        List<PublishingStatusRecord> recordList = new ArrayList<>();

        for(PublishingStatusItem item : itemList) {
           PublishingStatusRecord record = toPublishingStatusRecord(item);
            recordList.add(record);
        }
        return recordList;
    }
}
