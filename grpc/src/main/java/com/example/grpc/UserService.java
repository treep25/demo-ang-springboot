package com.example.grpc;

import com.example.grpc.proto.*;
import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import org.lognet.springboot.grpc.GRpcService;

import java.util.List;

@GRpcService
@RequiredArgsConstructor
public class UserService extends UserServiceGrpc.UserServiceImplBase {

    @Override
    public void getAllUsers(Empty request, StreamObserver<UserList> responseObserver) {
        responseObserver.onNext(UserList
                .newBuilder()
                .addAllUser(List.of(UserGrpc
                        .newBuilder()
                        .setUserId("UUID 123")
                        .setUsername("Pablo")
                        .setAge(18)
                        .build()))
                .build());

        responseObserver.onCompleted();
    }

    @Override
    public void getEntryById(UserIdRequest request, StreamObserver<UserGrpc> responseObserver) {
        responseObserver.onNext(UserGrpc
                .newBuilder()
                .setUserId("UUID 123")
                .setUsername("Pablo")
                .setAge(18)
                .build());

        responseObserver.onCompleted();
    }

    @Override
    public void createEntry(Empty request, StreamObserver<UserGrpc> responseObserver) {
        try {
            responseObserver.onNext(UserGrpc
                    .newBuilder()
                    .setUserId("UUID 123")
                    .setUsername("Pablo")
                    .setAge(18)
                    .build());

            responseObserver.onCompleted();
        } catch (Exception e) {
            responseObserver.onError(e);
        }
    }

    @Override
    public void deleteEntry(UserIdRequest request, StreamObserver<Empty> responseObserver) {
        responseObserver.onNext(Empty.newBuilder().build());

        responseObserver.onCompleted();
    }
}
