package ru.yandex.qatools.allure.teamcity;

import org.apache.maven.repository.internal.MavenRepositorySystemUtils;
import org.eclipse.aether.DefaultRepositorySystemSession;
import org.eclipse.aether.RepositorySystem;
import org.eclipse.aether.connector.file.FileRepositoryConnectorFactory;
import org.eclipse.aether.connector.wagon.WagonProvider;
import org.eclipse.aether.connector.wagon.WagonRepositoryConnectorFactory;
import org.eclipse.aether.impl.DefaultServiceLocator;
import org.eclipse.aether.impl.LocalRepositoryProvider;
import org.eclipse.aether.internal.impl.DefaultLocalRepositoryProvider;
import org.eclipse.aether.internal.impl.DefaultRepositorySystem;
import org.eclipse.aether.repository.LocalRepository;
import org.eclipse.aether.repository.LocalRepositoryManager;
import org.eclipse.aether.repository.RemoteRepository;
import org.eclipse.aether.spi.connector.RepositoryConnectorFactory;
import org.testng.annotations.Test;
import ru.yandex.qatools.allure.report.AllureReportBuilder;
import ru.yandex.qatools.allure.report.utils.AetherObjectFactory;
import ru.yandex.qatools.allure.report.utils.DependencyResolver;
import ru.yandex.qatools.allure.report.utils.ManualWagonProvider;

import java.io.File;
import java.util.Arrays;
import java.util.List;

import static ru.yandex.qatools.allure.report.utils.AetherObjectFactory.newRemoteRepository;

/**
 * eroshenkoam
 * 6/17/14
 */
public class SampleTest {

    @Test
    public void testOutput() throws Exception {
        File repos = new File("/Users/eroshenkoam/Downloads/repos");
        repos.mkdirs();

        DefaultServiceLocator locator = MavenRepositorySystemUtils.newServiceLocator();
        locator.addService(RepositoryConnectorFactory.class, WagonRepositoryConnectorFactory.class);
        locator.addService(RepositoryConnectorFactory.class, FileRepositoryConnectorFactory.class);
        locator.setServices(WagonProvider.class, new ManualWagonProvider());
        RepositorySystem repositorySystem = locator.getService(RepositorySystem.class);

        DefaultRepositorySystemSession session = MavenRepositorySystemUtils.newSession();
        LocalRepository localRepo = new LocalRepository(repos);
        System.out.println("Local Repos: " + localRepo);
        System.out.println("Session: " + session);

        File report = new File("/Users/eroshenkoam/Downloads/allure");
        report.mkdirs();

        LocalRepositoryManager manager = repositorySystem.newLocalRepositoryManager(session, localRepo);
        session.setLocalRepositoryManager(manager);
        List<RemoteRepository> remotes = Arrays.asList(newRemoteRepository(AetherObjectFactory.MAVEN_CENTRAL_URL));

        DependencyResolver resolver = new DependencyResolver(repositorySystem, session, remotes);
        AllureReportBuilder builder = new AllureReportBuilder("1.3.9", report, resolver);
        builder.processResults(new File("/Users/eroshenkoam/Downloads/allure-results/"));
        builder.unpackFace();
    }
}
