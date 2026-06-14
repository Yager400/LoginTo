/*
Copyright (C) 2026 Yager400

This file is part of this project, released under the terms of
the GNU General Public License v3.0.
See the LICENSE file for details.
 */
package net.loginto.bukkit.Utils.Premium;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import io.netty.handler.codec.MessageToMessageDecoder;

import javax.crypto.Cipher;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.List;

public class EncryptionUtils {

    public static KeyPair generateRSAPair() throws NoSuchAlgorithmException {
        KeyPairGenerator gen = KeyPairGenerator.getInstance("RSA");
        gen.initialize(1024);
        return gen.generateKeyPair();
    }

    public static byte[] generateVerifyToken() {
        SecureRandom random = new SecureRandom();
        byte[] verifyToken = new byte[4];
        random.nextBytes(verifyToken);
        return verifyToken;
    }

    public static class CipherDecoder extends MessageToMessageDecoder<ByteBuf> {
        private final Cipher cipher;

        public CipherDecoder(Cipher cipher) {
            this.cipher = cipher;
        }

        @Override
        protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
            byte[] bytes = new byte[in.readableBytes()];
            in.readBytes(bytes);
            byte[] decrypted = cipher.update(bytes);
            out.add(Unpooled.wrappedBuffer(decrypted));
        }
    }

    public static class CipherEncoder extends MessageToByteEncoder<ByteBuf> {
        private final Cipher cipher;

        public CipherEncoder(Cipher cipher) {
            this.cipher = cipher;
        }

        @Override
        protected void encode(ChannelHandlerContext ctx, ByteBuf in, ByteBuf out) throws Exception {
            byte[] bytes = new byte[in.readableBytes()];
            in.readBytes(bytes);
            out.writeBytes(cipher.update(bytes));
        }
    }
}
