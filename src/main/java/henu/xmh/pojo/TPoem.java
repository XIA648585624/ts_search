package henu.xmh.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
@Document(indexName = "ts", type = "poem")
public class TPoem implements Serializable {
    @Id
    private String id;

    @Field(type = FieldType.Text, analyzer = "ik_max_word")
    private String name;//诗词名

    @Field(type = FieldType.Keyword)
    private String author;//作者

    @Field(type = FieldType.Keyword)
    private String type;//类型

    @Field(type = FieldType.Text, analyzer = "ik_max_word")
    private String content;//内容

    @Field(type = FieldType.Keyword)
    private String href;//连接

    @Field(type = FieldType.Text, analyzer = "ik_max_word")
    private String authordes;//作者简介

    @Field(type = FieldType.Keyword)
    private String origin;//来源

    @Field(type = FieldType.Keyword)
    private String categoryid;//标签

    @Field(type = FieldType.Keyword)
    private String categoryname;//标签名
}