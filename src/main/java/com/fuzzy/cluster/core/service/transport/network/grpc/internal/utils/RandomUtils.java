package com.fuzzy.cluster.core.service.transport.network.grpc.internal.utils;

import java.security.SecureRandom;
import java.util.Random;

public class RandomUtils {

    public static Random random = new Random(new SecureRandom().nextLong());

}
