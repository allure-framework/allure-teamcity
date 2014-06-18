package ru.yandex.qatools.allure.teamcity;

import org.apache.maven.repository.internal.DefaultArtifactDescriptorReader;
import org.apache.maven.repository.internal.DefaultVersionRangeResolver;
import org.apache.maven.repository.internal.DefaultVersionResolver;
import org.apache.maven.repository.internal.MavenRepositorySystemUtils;
import org.eclipse.aether.DefaultRepositorySystemSession;
import org.eclipse.aether.connector.file.FileRepositoryConnectorFactory;
import org.eclipse.aether.connector.wagon.WagonProvider;
import org.eclipse.aether.connector.wagon.WagonRepositoryConnectorFactory;
import org.eclipse.aether.impl.*;
import org.eclipse.aether.internal.impl.*;
import org.eclipse.aether.repository.LocalRepository;
import org.eclipse.aether.repository.LocalRepositoryManager;
import org.eclipse.aether.repository.RemoteRepository;
import org.eclipse.aether.spi.connector.RepositoryConnectorFactory;
import org.eclipse.aether.spi.log.LoggerFactory;
import org.eclipse.aether.spi.log.NullLoggerFactory;
import ru.yandex.qatools.allure.report.utils.AetherObjectFactory;
import ru.yandex.qatools.allure.report.utils.DependencyResolver;
import ru.yandex.qatools.allure.report.utils.ManualWagonProvider;

import java.io.File;
import java.util.Arrays;
import java.util.List;

import static ru.yandex.qatools.allure.report.utils.AetherObjectFactory.newRemoteRepository;

/**
 * eroshenkoam
 * 6/18/14
 */
public class DependencyResolverBuilder {

    private DependencyResolverBuilder() {}

    public static DependencyResolver buildDependencyResolver(File mavenLocalDirectory) {
        DefaultRepositorySystem repositorySystem = new DefaultRepositorySystem();
        DefaultServiceLocator locator = MavenRepositorySystemUtils.newServiceLocator();

        locator.addService(RepositoryConnectorFactory.class, WagonRepositoryConnectorFactory.class);
        locator.addService(RepositoryConnectorFactory.class, FileRepositoryConnectorFactory.class);

        locator.setServices(WagonProvider.class, new ManualWagonProvider());
        locator.setService(LoggerFactory.class, NullLoggerFactory.class);
        locator.setService(VersionResolver.class, DefaultVersionResolver.class);
        locator.setService(VersionRangeResolver.class, DefaultVersionRangeResolver.class);
        locator.setService(ArtifactResolver.class, DefaultArtifactResolver.class);
        locator.setService(MetadataResolver.class, DefaultMetadataResolver.class);
        locator.setService(ArtifactDescriptorReader.class, DefaultArtifactDescriptorReader.class);
        locator.setService(DependencyCollector.class, DefaultDependencyCollector.class);
        locator.setService(Installer.class, DefaultInstaller.class);
        locator.setService(Deployer.class, DefaultDeployer.class);
        locator.setService(LocalRepositoryProvider.class, DefaultLocalRepositoryProvider.class);
        locator.setService(SyncContextFactory.class, DefaultSyncContextFactory.class);
        repositorySystem.initService(locator);
        DefaultRepositorySystemSession session = MavenRepositorySystemUtils.newSession();
        LocalRepository localRepo = new LocalRepository(mavenLocalDirectory.getAbsolutePath());
        LocalRepositoryManager repositoryManager = repositorySystem.newLocalRepositoryManager(session, localRepo);

        session.setLocalRepositoryManager(repositoryManager);

        List<RemoteRepository> remotes = Arrays.asList(newRemoteRepository(AetherObjectFactory.MAVEN_CENTRAL_URL));
        return new DependencyResolver(repositorySystem, session, remotes);
    }
}
