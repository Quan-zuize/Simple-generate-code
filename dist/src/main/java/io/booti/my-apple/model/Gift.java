package io.booti.my-apple.model;


import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.*;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import java.util.Date;

@Document(collection = "gm_gift")
@Getter
@Setter
public class Gift {

    @Field(value = "_id")
    private String id;

    @Field(value = "name")
    private String name;

    @Field(value = "status")
    private String status;

    @Field(value = "url")
    private String url;

    @Field(value = "created_by")
    @CreatedBy
    private String createdBy;

    @Field(value = "created_at")
    @CreatedDate
    private Date createdAt;

    @Field(value = "modified_at")
    @LastModifiedDate
    private Date modifiedAt;

    @Field(value = "modified_by")
    @LastModifiedBy
    private String modifiedBy;

}