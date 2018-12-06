package com.bosnet.ngemart.libgen;

import org.junit.Assert;
import org.junit.Test;

/**
 * Created by Luis Ginanjar on 12/04/2017.
 * Purpose :
 */

public class APIFailedExceptionTest {

    @Test
    public void test() {
        try {
            throw new APIFailedException("Hello");
        } catch (APIFailedException e) {
            Assert.assertEquals(e.getMessage(), "Hello");
        }
    }
}
