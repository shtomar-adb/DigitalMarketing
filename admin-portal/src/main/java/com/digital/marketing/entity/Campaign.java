package com.digital.marketing.entity;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAttribute;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBHashKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@DynamoDBTable(tableName = "Campaigns")
public class Campaign {

    @DynamoDBHashKey(attributeName = "campaign_id")
    private Integer id;

    @DynamoDBAttribute(attributeName = "campaign_name")
    private String name;

    @DynamoDBAttribute(attributeName = "campaign_content")
    private String content;

}
