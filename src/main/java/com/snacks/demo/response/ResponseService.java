package com.snacks.demo.response;

import org.springframework.stereotype.Service;

@Service
public class ResponseService {
    //반환할 데이터가 없을 때
    public CommonResponse getCommonResponse() {
        CommonResponse commonResponse = new CommonResponse();
        setSuccessResponse(commonResponse);

        return (commonResponse);
    }

    //반환할 데이터가 1개 일 떄
    public<T> SingleResponse<T> getSingleResponse(T data) {
        SingleResponse singleResponse = new SingleResponse();
        singleResponse.data = data;
        setSuccessResponse(singleResponse);


        return (singleResponse);
    }

    //반환할 데이터가 여러개 일 때
    public<T> ListResponse<T> getListResponse(T datalist) {
        ListResponse<T> listResponse = new ListResponse();
        listResponse.dataList = datalist;
        setSuccessResponse(listResponse);
        return (listResponse);
    }

    //오류를 반환할때
    public CommonResponse errorResponse(String log)
    {
        CommonResponse errorResponse = new CommonResponse();
        errorResponse.log = log;

        return (errorResponse);
    }
    //CommonResponse의 값을 성공으로 변환
    void setSuccessResponse(CommonResponse response) {
        response.log = "OK";
    }
}
