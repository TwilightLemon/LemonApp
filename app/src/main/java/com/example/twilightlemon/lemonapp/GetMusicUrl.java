package com.example.twilightlemon.lemonapp;

/**
 * Created by TwilightLemon on 2017/12/3.
 */

public class GetMusicUrl {
    public static String Get(String mid){
        try{
        String guid = "20D919A4D7700FBC424740E8CED80C5F";
        String ioo = HtmlService.getHtml("http://59.37.96.220/base/fcgi-bin/fcg_musicexpress2.fcg?version=12&miniversion=92&key=19914AA57A96A9135541562F16DAD6B885AC8B8B5420AC567A0561D04540172E&guid="+guid,true);
        String vkey = HtmlService.Fx(ioo, "key=\"", "\" speedrpttype");
        return "http://182.247.250.19/streamoc.music.tc.qq.com/M500"+mid+".mp3?vkey="+vkey+"&guid="+guid;}catch(Exception ex){return "";}
    }
}
