package com.digital.marketing.repository;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBScanExpression;
import com.digital.marketing.entity.Segment;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class SegmentRepository {

    @Autowired
    private DynamoDBMapper dynamoDBMapper;

    public Segment save(Segment segment) {
        dynamoDBMapper.save(segment);
        return segment;
    }

    public Segment getSegmentById(Integer id) {
        return dynamoDBMapper.load(Segment.class, id);
    }

    public List<Segment> getAllSegments() {
        return dynamoDBMapper.scan(Segment.class, new DynamoDBScanExpression());
    }

}
