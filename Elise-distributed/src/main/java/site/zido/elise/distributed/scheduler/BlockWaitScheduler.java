package site.zido.elise.distributed.scheduler;

import site.zido.elise.CrawlResult;
import site.zido.elise.Request;
import site.zido.elise.Task;
import site.zido.elise.scheduler.SimpleTaskScheduler;

import java.util.concurrent.Future;
import java.util.concurrent.Semaphore;

/**
 * BlockWaitScheduler is a concurrent blocking scheduler.
 * <p>
 * it based on {@link SimpleTaskScheduler},it can handle blockSize{@link BlockWaitScheduler#BlockWaitScheduler(int)} tasks as the same time.
 * When the number of concurrent tasks is blockSize, it will block until there are free threads to process.
 * <br/>
 * </p>
 *
 * @author zido
 */
public class BlockWaitScheduler extends SimpleTaskScheduler {
    private final Semaphore semaphore;

    public BlockWaitScheduler(int blockSize) {
        super(blockSize);
        semaphore = new Semaphore(blockSize);
    }

    @Override
    protected Future<CrawlResult> pushWhenNoDuplicate(Task task, Request request) {
        DownloadListener next = super.downloadListenerLoadBalancer.getNext();
        if (next == null) {
            throw new NullPointerException("no downloader");
        }
        try {
            semaphore.acquire();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        return super.rootExecutor.submit(() -> {
            try {
                return next.onDownload(task, request);
            } finally {
                semaphore.release();
            }
        });
    }
}
