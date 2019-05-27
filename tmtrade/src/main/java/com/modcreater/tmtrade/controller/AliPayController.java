package com.modcreater.tmtrade.controller;

import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.domain.AlipayTradeAppPayModel;
import com.alipay.api.request.AlipayTradeAppPayRequest;
import com.modcreater.tmdao.mapper.OrderMapper;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

import static com.alipay.api.AlipayConstants.APP_ID;
import static com.alipay.api.AlipayConstants.CHARSET;

/**
 * Created with IntelliJ IDEA.
 * Description:
 *
 * @Author: Goku_yi
 * @Date: 2019-05-24
 * Time: 17:42
 */
@RestController
public class AliPayController {
    private AlipayClient client = new DefaultAlipayClient("https://openapi.alipay.com/gateway.do", "2016092400587194", "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAnjhl5phc84kvWiVOCp3AMLz2MHvFl9AbPXTfRQXtU8oGiFXRFQtBsx/W+wE7bXNxQRjYLcYgnyrGD8ISVq+IcpXS3hRN+VyHVXmyPid53TIjSm95VFE8aKVmPvKAbYLw2Onshb5k7pbFKrB6T44kdJerIUX1bp6pWdmQpm7LuQIj9GHY5D+GT3GME37tEsKzeE/AtJLrpnZiZyF5JxwMlYw0VXaoug+aEvERZKJs7ojJuAwrwzk+avEFtkhjg0pQLOPgd8G5ED2RG3NiXKK6ZEfAFExlFDFoJhgZpRP0/kRVUon7sqq3Any2YWAxbrShYpq1bWVspCQaELqm+sobHQIDAQAB", "json", CHARSET, "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQDIgHnOn7LLILlKETd6BFRJ0GqgS2Y3mn1wMQmyh9zEyWlz5p1zrahRahbXAfCfSqshSNfqOmAQzSHRVjCqjsAw1jyqrXaPdKBmr90DIpIxmIyKXv4GGAkPyJ/6FTFY99uhpiq0qadD/uSzQsefWo0aTvP/65zi3eof7TcZ32oWpwIDAQAB", "RSA2");

    private AlipayTradeAppPayRequest request = new AlipayTradeAppPayRequest();

    private AlipayTradeAppPayModel model = new AlipayTradeAppPayModel();

    @Resource
    private OrderMapper orderMapper;

    @PostMapping(value = "alipay")
    public void aliPayOrderSubmitted(){

    }



}
