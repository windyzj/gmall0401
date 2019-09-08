package com.atguigu.gmall0401.bean;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Id;
import java.io.Serializable;

@NoArgsConstructor
@Data
public class BaseSaleAttr implements Serializable {
    @Id
    @Column
    String id ;

    @Column
    String name;
}

