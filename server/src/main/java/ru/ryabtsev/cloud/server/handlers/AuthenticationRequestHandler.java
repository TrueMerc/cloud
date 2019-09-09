package ru.ryabtsev.cloud.server.handlers;

import io.netty.channel.ChannelHandlerContext;
import ru.ryabtsev.cloud.common.message.Operations;
import ru.ryabtsev.cloud.common.message.client.AuthenticationRequest;
import ru.ryabtsev.cloud.common.message.server.AuthenticationResponse;
import ru.ryabtsev.cloud.server.service.UserService;

public class AuthenticationRequestHandler implements Handler {
    private final ChannelHandlerContext context;
    private final UserService userService;
    private final AuthenticationRequest request;

    AuthenticationRequestHandler(ChannelHandlerContext context, UserService service, AuthenticationRequest request) {
        this.context = context;
        this.userService = service;
        this.request = request;
    }

    @Override
    public void handle() {
        boolean result = userService.authenticate(request.getLogin(), request.getPassword());
        if(result) {
            final String userLogin = request.getLogin();
            final String userRootFolder = userService.getRootFolder(userLogin);
            final String userCurrentFolder = userService.getCurrentFolder(userLogin);
        }
        context.writeAndFlush(new AuthenticationResponse(true));
    }
}
