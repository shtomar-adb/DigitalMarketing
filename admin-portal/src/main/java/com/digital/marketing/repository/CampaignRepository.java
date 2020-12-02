package com.digital.marketing.repository;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBScanExpression;
import com.digital.marketing.entity.Campaign;
import com.digital.marketing.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class CampaignRepository {

    @Autowired
    private DynamoDBMapper dynamoDBMapper;

    public Campaign save(Campaign campaign) {
        dynamoDBMapper.save(campaign);
        return campaign;
    }

    public Campaign getCampaignById(Integer id) {
        return dynamoDBMapper.load(Campaign.class, id);
    }

    public List<Campaign> getAllCampaigns() {
        return dynamoDBMapper.scan(Campaign.class, new DynamoDBScanExpression());
    }

}
