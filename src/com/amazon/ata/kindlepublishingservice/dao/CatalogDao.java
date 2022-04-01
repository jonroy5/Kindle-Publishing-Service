package com.amazon.ata.kindlepublishingservice.dao;

import com.amazon.ata.kindlepublishingservice.dynamodb.models.CatalogItemVersion;
import com.amazon.ata.kindlepublishingservice.exceptions.BookNotFoundException;
import com.amazon.ata.kindlepublishingservice.models.Book;
import com.amazon.ata.kindlepublishingservice.publishing.KindleFormattedBook;
import com.amazon.ata.kindlepublishingservice.utils.KindlePublishingUtils;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBQueryExpression;
import org.apache.commons.lang3.StringUtils;

import java.util.List;
import javax.inject.Inject;

public class CatalogDao {

    private final DynamoDBMapper dynamoDbMapper;

    /**
     * Instantiates a new CatalogDao object.
     *
     * @param dynamoDbMapper The {@link DynamoDBMapper} used to interact with the catalog table.
     */
    @Inject
    public CatalogDao(DynamoDBMapper dynamoDbMapper) {
        this.dynamoDbMapper = dynamoDbMapper;
    }

    /**
     * Returns the latest version of the book from the catalog corresponding to the specified book id.
     * Throws a BookNotFoundException if the latest version is not active or no version is found.
     * @param bookId Id associated with the book.
     * @return The corresponding CatalogItem from the catalog table.
     */
    public CatalogItemVersion getBookFromCatalog(String bookId) {
        CatalogItemVersion book = getLatestVersionOfBook(bookId);

        if (book == null || book.isInactive()) {
            throw new BookNotFoundException(String.format("No book found for id: %s", bookId));
        }

        return book;
    }

    // Returns null if no version exists for the provided bookId
    private CatalogItemVersion getLatestVersionOfBook(String bookId) {


        CatalogItemVersion book = new CatalogItemVersion();
        book.setBookId(bookId);


        DynamoDBQueryExpression<CatalogItemVersion> queryExpression = new DynamoDBQueryExpression<CatalogItemVersion>()
            .withHashKeyValues(book)
            .withScanIndexForward(false)
            .withLimit(1);


        List<CatalogItemVersion> results = dynamoDbMapper.query(CatalogItemVersion.class, queryExpression);
        if (results.isEmpty()) {
            return null;
        }
        return results.get(0);
    }
    public CatalogItemVersion removeBookFromCatalog(String bookId) {
        CatalogItemVersion book = getLatestVersionOfBook(bookId);

        if (book == null || book.isInactive()) {
            throw new BookNotFoundException(String.format("No book found for id: %s", bookId));

        }
        book.setInactive(true);
        dynamoDbMapper.save(book);

        return book;

    }

    public void validateBookExists(String bookId) {
        CatalogItemVersion book = getLatestVersionOfBook(bookId);

        if (book == null) {
            throw new BookNotFoundException(String.format("No book found for id: %s", bookId));
        }

    }

    public CatalogItemVersion createOrUpdateBook(KindleFormattedBook kindleBook) {

        CatalogItemVersion book = new CatalogItemVersion();
        if(kindleBook.getBookId() == null) {
            book.setBookId(KindlePublishingUtils.generateBookId());
            book.setAuthor(kindleBook.getAuthor());
            book.setGenre(kindleBook.getGenre());
            book.setTitle(kindleBook.getTitle());
            book.setText(kindleBook.getText());
            book.setVersion(1);
            dynamoDbMapper.save(book);
            return book;

        } else {
           CatalogItemVersion alreadyABook = getBookFromCatalog(kindleBook.getBookId());
            removeBookFromCatalog(alreadyABook.getBookId());
            alreadyABook.setBookId(kindleBook.getBookId());
            alreadyABook.setAuthor(kindleBook.getAuthor());
            alreadyABook.setGenre(kindleBook.getGenre());
            alreadyABook.setTitle(kindleBook.getTitle());
            alreadyABook.setText(kindleBook.getText());
            alreadyABook.setVersion(alreadyABook.getVersion()+1);
            dynamoDbMapper.save(alreadyABook);
            return alreadyABook;
        }



    }
}
