package com.jsnjfz.manage.modular.system.enums;

import java.util.Arrays;

/**
 * @Author qyh
 * @Date 2023/4/29 18:58
 * @Description
 */
public enum AddressEnum {
    ICON(0,"icon/"),
    HEAD(1,"head/"),
    GIF(2,"gif/"),
    PHONEBACK(3,"phoneBack/"),
    FRIEND(4,"friend/"),
    EMOJI(5,"emoji/"),
    WATCH(6,"iwatch/"),
    SD(7,"sd/"),
    LOVE_HEAD(8,"loveHead/"),
    LINKS(9,"links/"),
    SITE(10,"site/");


    private Integer businessType;
    private String fileAddress;

    AddressEnum(Integer businessType, String fileAddress) {
        this.businessType = businessType;
        this.fileAddress = fileAddress;
    }

    public Integer getBusinessType() {
        return businessType;
    }

    public void setBusinessType(Integer businessType) {
        this.businessType = businessType;
    }

    public String getFileAddress() {
        return fileAddress;
    }

    public void setFileAddress(String fileAddress) {
        this.fileAddress = fileAddress;
    }

    public static AddressEnum getMessageEnum(Integer businessType) {
        return Arrays.stream(AddressEnum.values()).filter(x -> x.businessType.equals(businessType)).findFirst().orElse(null);
    }
}
