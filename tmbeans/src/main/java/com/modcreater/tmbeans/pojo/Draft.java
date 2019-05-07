package com.modcreater.tmbeans.pojo;

import lombok.Data;

import java.io.Serializable;

@Data
public class Draft implements Serializable {

  private long id;
  private String phoneNum;
  private String data;

}
