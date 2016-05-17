package com.nit.weixi.study_c_system;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * To work on unit tests, switch the Test Artifact in the Build Variants view.
 */
public class ExampleUnitTest {
    @Test
    public void addition_isCorrect() throws Exception {
        assertEquals(4, 2 + 2);
    }

    @Test
    public void test1(){
        System.out.println(formatTime(10*60*1000));
    }

    public String formatTime(long haomiao){
        int miao= (int) (haomiao/1000);
        int fen=miao/60;
        int yu=miao%60;
        return fen+"分"+yu+"秒";
    }
}