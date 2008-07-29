package org.jetbrains.idea.maven.indices;

import com.intellij.openapi.progress.EmptyProgressIndicator;
import com.intellij.openapi.progress.ProcessCanceledException;
import org.apache.maven.embedder.MavenEmbedder;
import org.jetbrains.idea.maven.MavenTestCase;
import org.jetbrains.idea.maven.core.util.MavenId;
import org.jetbrains.idea.maven.embedder.MavenEmbedderFactory;

import java.io.File;
import java.util.concurrent.atomic.AtomicBoolean;

public abstract class MavenIndicesStressTest extends MavenTestCase {
  public void test() throws Exception {
    MavenCustomRepositoryTestFixture fixture;

    fixture = new MavenCustomRepositoryTestFixture(myDir);
    fixture.setUp();
    fixture.copy("plugins", "local1");
    fixture.copy("local2", "local1");
    //setRepositoryPath(fixture.getTestDataPath("local1"));

    MavenEmbedder embedder = MavenEmbedderFactory.createEmbedderForExecute(getMavenCoreSettings()).getEmbedder();
    File indicesDir = new File(myDir, "indices");

    final MavenIndices indices = new MavenIndices(embedder, indicesDir);
    final MavenIndex index = indices.add(getRepositoryPath(), MavenIndex.Kind.LOCAL);

    final AtomicBoolean isFinished = new AtomicBoolean(false);

    Thread t1 = new Thread(new Runnable() {
      public void run() {
        try {
          for (int i = 0; i < 3; i++) {
            System.out.println("INDEXING #" + i);
            indices.update(index, new EmptyProgressIndicator());
          }
        }
        catch (ProcessCanceledException e) {
          throw new RuntimeException(e);
        }
        isFinished.set(true);
      }
    });

    Thread t2 = new Thread(new Runnable() {
      public void run() {
        try {
          int i = 0;
          while (!isFinished.get()) {
            System.out.println("Adding artifact #" + i);
            index.addArtifact(new MavenId("group" + i, "artifact" + 1, "" + 1));
            i++;
          }
        }
        catch (MavenIndexException e) {
          throw new RuntimeException(e);
        }
      }
    });

    t1.start();
    t2.start();

    do {
      t1.join(100);
      t2.join(100);
    }
    while (!isFinished.get());

    t1.join(100);
    t2.join(100);
    
    indices.close();
  }
}
