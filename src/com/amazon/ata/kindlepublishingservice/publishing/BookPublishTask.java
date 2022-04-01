package com.amazon.ata.kindlepublishingservice.publishing;

import com.amazon.ata.aws.dynamodb.DynamoDbClientProvider;
import com.amazon.ata.kindlepublishingservice.dao.CatalogDao;
import com.amazon.ata.kindlepublishingservice.dao.PublishingStatusDao;
import com.amazon.ata.kindlepublishingservice.dynamodb.models.CatalogItemVersion;
import com.amazon.ata.kindlepublishingservice.dynamodb.models.PublishingStatusItem;
import com.amazon.ata.kindlepublishingservice.enums.PublishingRecordStatus;
import com.amazon.ata.kindlepublishingservice.exceptions.BookNotFoundException;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;

import javax.inject.Inject;


public class BookPublishTask implements Runnable {
    private BookPublishRequestManager requestManager;
    private PublishingStatusDao statusDao;
    private CatalogDao catalogDao;

    @Inject
    public BookPublishTask(BookPublishRequestManager requestManager, PublishingStatusDao statusDao, CatalogDao catalogDao) {
        this.statusDao = statusDao;
        this.requestManager = requestManager;
        this.catalogDao = catalogDao;

    }

    public void run() {

       BookPublishRequest newRequest = requestManager.getBookPublishRequestToProcess();
       while(newRequest == null) {
           try {
               Thread.sleep(1000);
               newRequest = requestManager.getBookPublishRequestToProcess();
           } catch (InterruptedException e) {
               e.printStackTrace();
           }
       }

       PublishingStatusItem item = statusDao.setPublishingStatus(newRequest.getPublishingRecordId(), PublishingRecordStatus.IN_PROGRESS, newRequest.getBookId());


        KindleFormattedBook kindleFormattedBook = KindleFormatConverter.format(newRequest);

        try {
            CatalogItemVersion book = catalogDao.createOrUpdateBook(kindleFormattedBook);
            item = statusDao.setPublishingStatus(newRequest.getPublishingRecordId(), PublishingRecordStatus.SUCCESSFUL, book.getBookId());
        } catch (BookNotFoundException e) {
            item = statusDao.setPublishingStatus(newRequest.getPublishingRecordId(), PublishingRecordStatus.FAILED, newRequest.getBookId());
        }




    }
}
