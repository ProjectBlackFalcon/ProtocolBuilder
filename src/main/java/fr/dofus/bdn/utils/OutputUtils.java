package fr.dofus.bdn.utils;

import java.util.Collections;
import java.util.concurrent.TimeUnit;

public class OutputUtils {

    private static Long startTime;
    private static Long total;
    private static Long current;

    public static void printProgress() {
        if (startTime == null || total == null || current == null){
            throw new Error("Please init startTime and total");
        }

        long eta = current == 0 ? 0 :
            (total - current) * (System.currentTimeMillis() - startTime) / current;

        String etaHms = current == 0 ? "N/A" :
            String.format("%02d:%02d:%02d", TimeUnit.MILLISECONDS.toHours(eta),
                TimeUnit.MILLISECONDS.toMinutes(eta) % TimeUnit.HOURS.toMinutes(1),
                TimeUnit.MILLISECONDS.toSeconds(eta) % TimeUnit.MINUTES.toSeconds(1));

        StringBuilder string = new StringBuilder(140);
        int percent = (int) (current * 100 / total);
        string
            .append('\r')
            .append(String.join("", Collections.nCopies(percent == 0 ? 2 : 2 - (int) (Math.log10(percent)), " ")))
            .append(String.format(" %d%% [", percent))
            .append(String.join("", Collections.nCopies(percent, "=")))
            .append('>')
            .append(String.join("", Collections.nCopies(100 - percent, " ")))
            .append(']')
            .append(String.join("", Collections.nCopies((int) (Math.log10(total)) - (int) (Math.log10(current)), " ")))
            .append(String.format(" %d/%d, ETA: %s", current, total, etaHms));

        System.out.print(string);
        current++;

        if (current == total + 1) {
            System.out.println();
        }
    }

    public static void init (final Long startTime, final Long total){
        OutputUtils.startTime = startTime;
        OutputUtils.total = total;
        OutputUtils.current = 1L;
    }
}
