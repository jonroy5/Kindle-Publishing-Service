package com.amazon.ata.kindlepublishingservice.activity;

import com.amazon.ata.aws.dynamodb.DynamoDbClientProvider;
import com.amazon.ata.kindlepublishingservice.converters.CatalogItemConverter;
import com.amazon.ata.kindlepublishingservice.dao.CatalogDao;
import com.amazon.ata.kindlepublishingservice.dynamodb.models.CatalogItemVersion;
import com.amazon.ata.kindlepublishingservice.models.requests.RemoveBookFromCatalogRequest;
import com.amazon.ata.kindlepublishingservice.models.response.RemoveBookFromCatalogResponse;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.lambda.runtime.Context;

import javax.inject.Inject;
import javax.xml.catalog.Catalog;

public class RemoveBookFromCatalogActivity {
    @Inject
    RemoveBookFromCatalogActivity() {}
    public RemoveBookFromCatalogResponse execute(RemoveBookFromCatalogRequest removeBookFromCatalogRequest) {
        DynamoDBMapper dynamoDBMapper = new DynamoDBMapper(DynamoDbClientProvider.getDynamoDBClient());
        CatalogDao catalogDao = new CatalogDao(dynamoDBMapper);
        CatalogItemVersion book = catalogDao.removeBookFromCatalog(removeBookFromCatalogRequest.getBookId());

        return RemoveBookFromCatalogResponse.builder()
                .withBook(CatalogItemConverter.toBook(book))
                .build();
    }
}
