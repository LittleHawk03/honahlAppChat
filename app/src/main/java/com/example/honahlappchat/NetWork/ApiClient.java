package com.example.honahlappchat.NetWork;

import retrofit2.Retrofit;
import retrofit2.converter.scalars.ScalarsConverterFactory;

/**
 *  Retrofit là một công nghệ được phát triển bởi Square,
 *  nó được xây dựng dựa trên rất nhiều công nghệ mạnh mẽ cho phép giải quyết tốt các yêu cầu từ phía client
 *  và server một cách nhanh và hiệu quả nhất.
 *  Tóm lại, Retrofit là một REST Client dành Android và cả Java. Retrofit được phát triển giúp cho quá trình
 *  kết nối client – server trở nên dễ dàng, nhanh chóng. Đối với Retrofit bạn có thể GET, POST, PUT, DELETE
 * */


public class ApiClient {

    private static Retrofit retrofit = null;

    public static Retrofit getClient(){
        if (retrofit == null){
            retrofit = new Retrofit.Builder()
                    .baseUrl("https://fcm.googleapis.com/fcm/")
                    .addConverterFactory(ScalarsConverterFactory.create())
                    .build();
        }
        return retrofit;
    }
}
