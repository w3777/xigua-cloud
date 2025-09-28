/*
* Licensed to the Apache Software Foundation (ASF) under one or more
* contributor license agreements.  See the NOTICE file distributed with
* this work for additional information regarding copyright ownership.
* The ASF licenses this file to You under the Apache License, Version 2.0
* (the "License"); you may not use this file except in compliance with
* the License.  You may obtain a copy of the License at
*
*     http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/

package com.xigua.api.service;

import com.google.protobuf.Message;
import org.apache.dubbo.common.URL;
import org.apache.dubbo.rpc.Invoker;
import org.apache.dubbo.rpc.PathResolver;
import org.apache.dubbo.rpc.RpcException;
import org.apache.dubbo.rpc.ServerService;
import org.apache.dubbo.rpc.TriRpcStatus;
import org.apache.dubbo.rpc.model.MethodDescriptor;
import org.apache.dubbo.rpc.model.ServiceDescriptor;
import org.apache.dubbo.rpc.model.StubMethodDescriptor;
import org.apache.dubbo.rpc.model.StubServiceDescriptor;
import org.apache.dubbo.reactive.handler.ManyToManyMethodHandler;
import org.apache.dubbo.reactive.handler.ManyToOneMethodHandler;
import org.apache.dubbo.reactive.handler.OneToManyMethodHandler;
import org.apache.dubbo.reactive.calls.ReactorClientCalls;
import org.apache.dubbo.reactive.handler.OneToOneMethodHandler;

import org.apache.dubbo.rpc.stub.StubInvoker;
import org.apache.dubbo.rpc.stub.StubMethodHandler;
import org.apache.dubbo.rpc.stub.StubSuppliers;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.Map;

public final class DubboAIServiceTriple {

    private DubboAIServiceTriple() {}

    public static final String SERVICE_NAME = AIService.SERVICE_NAME;

    private static final StubServiceDescriptor serviceDescriptor = new StubServiceDescriptor(SERVICE_NAME,AIService.class);

    static {
        org.apache.dubbo.rpc.protocol.tri.service.SchemaDescriptorRegistry.addSchemaDescriptor(SERVICE_NAME,AIServiceProto.getDescriptor());
        StubSuppliers.addSupplier(SERVICE_NAME, DubboAIServiceTriple::newStub);
        StubSuppliers.addSupplier(AIService.JAVA_SERVICE_NAME,  DubboAIServiceTriple::newStub);
        StubSuppliers.addDescriptor(SERVICE_NAME, serviceDescriptor);
        StubSuppliers.addDescriptor(AIService.JAVA_SERVICE_NAME, serviceDescriptor);
    }

    @SuppressWarnings("all")
    public static AIService newStub(Invoker<?> invoker) {
        return new AIServiceStub((Invoker<AIService>)invoker);
    }


        /**
         * <pre>
         *  输入的内容
         * </pre>
         */
    private static final StubMethodDescriptor chatMethod = new StubMethodDescriptor("Chat",
        com.xigua.api.service.ChatRequest.class, com.xigua.api.service.ChatResponse.class, MethodDescriptor.RpcType.SERVER_STREAM,
        obj -> ((Message) obj).toByteArray(), obj -> ((Message) obj).toByteArray(), com.xigua.api.service.ChatRequest::parseFrom,
        com.xigua.api.service.ChatResponse::parseFrom);



    static{
        serviceDescriptor.addMethod(chatMethod);
    }

    public static class AIServiceStub implements AIService{

        private final Invoker<AIService> invoker;

        public AIServiceStub(Invoker<AIService> invoker) {
            this.invoker = invoker;
        }

            /**
         * <pre>
         *  输入的内容
         * </pre>
         */
        public Flux<com.xigua.api.service.ChatResponse> chat(Mono<com.xigua.api.service.ChatRequest> request) {
            return ReactorClientCalls.oneToMany(invoker, request, chatMethod);
        }
    }

    public static abstract class AIServiceImplBase implements AIService, ServerService<AIService> {

        @Override
        public final Invoker<AIService> getInvoker(URL url) {
            PathResolver pathResolver = url.getOrDefaultFrameworkModel()
            .getExtensionLoader(PathResolver.class)
            .getDefaultExtension();
            Map<String,StubMethodHandler<?, ?>> handlers = new HashMap<>();

                pathResolver.addNativeStub( "/" + SERVICE_NAME + "/Chat");
                // for compatibility
                pathResolver.addNativeStub( "/" + JAVA_SERVICE_NAME + "/Chat");

                handlers.put(chatMethod.getMethodName(), new OneToManyMethodHandler<>(this::chat));

            return new StubInvoker<>(this, url, AIService.class, handlers);
        }

            /**
         * <pre>
         *  输入的内容
         * </pre>
         */
        public Flux<com.xigua.api.service.ChatResponse> chat(Mono<com.xigua.api.service.ChatRequest> request) {
            throw unimplementedMethodException(chatMethod);
        }

        @Override
        public final ServiceDescriptor getServiceDescriptor() {
            return serviceDescriptor;
        }

        private RpcException unimplementedMethodException(StubMethodDescriptor methodDescriptor) {
            return TriRpcStatus.UNIMPLEMENTED.withDescription(String.format("Method %s is unimplemented",
            "/" + serviceDescriptor.getInterfaceName() + "/" + methodDescriptor.getMethodName())).asException();
        }
    }
}
