package rebue.wheel;

import java.util.LinkedHashMap;
import java.util.Map;

import org.junit.Test;

public class XmlUtilsTest {

    @Test
    public void test01() {
//        String text = "transaction_id=4200000166201809186779456024, nonce_str=D3F05A7C86E44F7CBE96DF6ED1E58D71, bank_type=CFT, openid=onCVJv4wryxQkdwfkVtIk4riqFaY, sign=5E979E8140E8B2E75D65202944154FCB, fee_type=CNY, mch_id=1444599902, cash_fee=350, out_trade_no=383505551837000148, appid=wx9e24a0de9e3e136c, total_fee=350, trade_type=JSAPI, result_code=SUCCESS, attach=516432142578745977, time_end=20180918112033, is_subscribe=Y, return_code=SUCCESS";
//        String text = "transaction_id=4200000169201809186988845355, nonce_str=C4B51961BE9A4D6F84376A2A844B431B, bank_type=CFT, openid=onCVJv66YbKB6aKQZXuucD5wtSEs, sign=35CC0DB6308AF7EA385248B3D2F7E1C7, fee_type=CNY, mch_id=1444599902, cash_fee=400, out_trade_no=384156003883000521, appid=wx9e24a0de9e3e136c, total_fee=400, trade_type=JSAPI, result_code=SUCCESS, attach=451969149627793446, time_end=20180918113015, is_subscribe=Y, return_code=SUCCESS";
//        String text = "transaction_id=4200000176201809180988691379, nonce_str=8A0F5DCCE88C4919A8FCEA33717BD825, bank_type=CFT, openid=onCVJvzUpk_hxAe2_PZPoUzv1QX8, sign=36D9A2E506CDB484D5A1759E28B7DAF6, fee_type=CNY, mch_id=1444599902, cash_fee=200, out_trade_no=384657168337000204, appid=wx9e24a0de9e3e136c, total_fee=200, trade_type=JSAPI, result_code=SUCCESS, attach=93126, time_end=20180918113250, is_subscribe=Y, return_code=SUCCESS";
//        String text = "transaction_id=4200000164201809182777058471, nonce_str=B3206767E5DA44CA980BA0F1ECD2E17F, bank_type=CFT, openid=onCVJv4wryxQkdwfkVtIk4riqFaY, sign=A96DAC00393ECB15210FD14D088929D6, fee_type=CNY, mch_id=1444599902, cash_fee=250, out_trade_no=387825634571000761, appid=wx9e24a0de9e3e136c, total_fee=250, trade_type=JSAPI, result_code=SUCCESS, attach=516432142578745977, time_end=20180918113939, is_subscribe=Y, return_code=SUCCESS";
//        String text = "transaction_id=4200000166201809186779456024, nonce_str=D3F05A7C86E44F7CBE96DF6ED1E58D71, bank_type=CFT, openid=onCVJv4wryxQkdwfkVtIk4riqFaY, sign=5E979E8140E8B2E75D65202944154FCB, fee_type=CNY, mch_id=1444599902, cash_fee=350, out_trade_no=383505551837000148, appid=wx9e24a0de9e3e136c, total_fee=350, trade_type=JSAPI, result_code=SUCCESS, attach=516432142578745977, time_end=20180918112033, is_subscribe=Y, return_code=SUCCESS";
//        String text = "transaction_id=4200000168201809184228609102, nonce_str=0E56A235B2624AA99040BBE2CD2F5F25, bank_type=CFT, openid=onCVJv-B-9g1a5FcUxtI1bXqGWEo, sign=D664D34FBC33E20500E29412577E9346, fee_type=CNY, mch_id=1444599902, cash_fee=300, out_trade_no=384201127774000114, appid=wx9e24a0de9e3e136c, total_fee=300, trade_type=JSAPI, result_code=SUCCESS, attach=220452, time_end=20180918134514, is_subscribe=Y, return_code=SUCCESS";
//        String text = "transaction_id=4200000188201809187966310177, nonce_str=6DFF4897E5B74F21A579AF641A1AA472, bank_type=CFT, openid=onCVJv-EuAtwqm5xxmbERIH56Kdk, sign=F53F0CB5B0C82E8B9282318DD88B9BB3, fee_type=CNY, mch_id=1444599902, cash_fee=550, out_trade_no=387577316008000261, appid=wx9e24a0de9e3e136c, total_fee=550, trade_type=JSAPI, result_code=SUCCESS, attach=509919228539699570, time_end=20180918142504, is_subscribe=N, return_code=SUCCESS";
//        String text = "transaction_id=4200000176201809189786311076, nonce_str=47735F97015B4D12968DDBC371FD6376, bank_type=CFT, openid=onCVJvytrGhWX7nd2z1N7NxTYuYk, sign=AA69A4C01D356B864AFC0776D34B5DE4, fee_type=CNY, mch_id=1444599902, cash_fee=350, out_trade_no=388641655305000122, appid=wx9e24a0de9e3e136c, total_fee=350, trade_type=JSAPI, result_code=SUCCESS, attach=517217669091426966, time_end=20180918154054, is_subscribe=N, return_code=SUCCESS";
//        String text = "transaction_id=4200000167201809186476001168, nonce_str=540645B2EC814CE9825B659FD7A48497, bank_type=CFT, openid=onCVJv9BS0LaDCAemoFk4ZnRc_JE, sign=97914525FEBDD25ADA567314DE41A4F9, fee_type=CNY, mch_id=1444599902, cash_fee=600, out_trade_no=383148453493000218, appid=wx9e24a0de9e3e136c, total_fee=600, trade_type=JSAPI, result_code=SUCCESS, attach=517218165734769303, time_end=20180918154245, is_subscribe=N, return_code=SUCCESS";
//        String text = "transaction_id=4200000183201809184194144181, nonce_str=E15A96B170D54A54AC36481EF0459540, bank_type=CFT, openid=onCVJvzUpk_hxAe2_PZPoUzv1QX8, sign=D52B2EA03EB6E6713C9A539F600ED53D, fee_type=CNY, mch_id=1444599902, cash_fee=300, out_trade_no=386554316602000751, appid=wx9e24a0de9e3e136c, total_fee=300, trade_type=JSAPI, result_code=SUCCESS, attach=93126, time_end=20180918162527, is_subscribe=Y, return_code=SUCCESS";
//        String text = "transaction_id=4200000184201809181331530557, nonce_str=96A17FA67C2B42D28915E6D812F494E1, bank_type=ABC_DEBIT, openid=onCVJv6sk8Lye7ycrgFsYzb6FULE, sign=63B9A3E4A8DA58368AEAC6F3159430DD, fee_type=CNY, mch_id=1444599902, cash_fee=700, out_trade_no=380047785024000767, appid=wx9e24a0de9e3e136c, total_fee=700, trade_type=JSAPI, result_code=SUCCESS, attach=503006616900075722, time_end=20180918162748, is_subscribe=Y, return_code=SUCCESS";
        String text = "transaction_id=4200000186201809183104700168, nonce_str=EC12F2695A8C43F4B751112E1FF1B7F6, bank_type=ICBC_DEBIT, openid=onCVJvxC56pwHxEDKOKdp_4GDSNY, sign=BFBFA1A0523C397FA815CCFDE665AAF1, fee_type=CNY, mch_id=1444599902, cash_fee=300, out_trade_no=388168756993000016, appid=wx9e24a0de9e3e136c, total_fee=300, trade_type=JSAPI, result_code=SUCCESS, attach=193201, time_end=20180918165005, is_subscribe=Y, return_code=SUCCESS";
        String[] split = text.split(",");
        Map<String, Object> map = new LinkedHashMap<>();
        for (String item : split) {
            String[] kv = item.split("=");
            map.put(kv[0].trim(), kv[1].trim());
        }
        System.out.println(XmlUtils.mapToXml(map));
    }
}
