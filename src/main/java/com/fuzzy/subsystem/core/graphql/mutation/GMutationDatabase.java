package com.fuzzy.subsystem.core.graphql.mutation;

import com.fuzzy.main.cluster.core.remote.struct.RemoteObject;
import com.fuzzy.main.cluster.graphql.anotation.GraphQLDescription;
import com.fuzzy.main.cluster.graphql.anotation.GraphQLField;
import com.fuzzy.main.cluster.graphql.anotation.GraphQLName;
import com.fuzzy.main.cluster.graphql.anotation.GraphQLTypeOutObject;
import com.fuzzy.main.cluster.graphql.struct.GOptional;
import com.infomaximum.main.Subsystems;
import com.fuzzy.main.platform.Platform;
import com.fuzzy.main.platform.component.database.DatabaseComponent;
import com.fuzzy.main.platform.component.database.DatabaseConsts;
import com.fuzzy.main.platform.component.database.remote.backup.RControllerBackup;
import com.fuzzy.main.platform.component.database.remote.backup.RControllerRestore;
import com.fuzzy.main.platform.component.frontend.context.ContextTransactionRequest;
import com.fuzzy.main.platform.exception.PlatformException;
import com.fuzzy.main.platform.querypool.Query;
import com.fuzzy.main.platform.querypool.QueryTransaction;
import com.fuzzy.main.platform.querypool.ResourceProvider;
import com.fuzzy.main.platform.sdk.graphql.annotation.GraphQLAuthControl;
import com.fuzzy.main.platform.sdk.graphql.customfield.graphqlquery.GraphQLQuery;
import com.fuzzy.subsystem.core.CoreSubsystem;
import com.fuzzy.subsystem.core.access.CorePrivilege;
import com.fuzzy.subsystem.core.graphql.query.depersonalization.DepersonalizationStage;
import com.fuzzy.subsystem.core.graphql.query.depersonalization.GQueryDepersonalization;
import com.fuzzy.subsystem.core.remote.depersonalization.Options;
import com.fuzzy.subsystem.core.remote.depersonalization.RControllerDepersonalization;
import com.fuzzy.subsystem.database.DatabaseComponentExtensionImpl;
import com.fuzzy.subsystem.frontend.authcontext.AuthorizedContext;
import com.fuzzy.subsystems.access.AccessOperation;
import com.fuzzy.subsystems.exception.GeneralExceptionBuilder;
import com.fuzzy.subsystems.graphql.query.GAccessQuery;
import com.infomaximum.utils.FileUtils;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.*;
import java.util.Set;

@GraphQLTypeOutObject("mutation_database")
public class GMutationDatabase {

	private static final Logger log = LoggerFactory.getLogger(GMutationDatabase.class);

	private static final String BACKUP_NAME = "backup_name";
	private static final String BACKUP_DIR_PATH = "backup_dir_path";
	private static final String PATH = "path";

	@GraphQLField
	@GraphQLAuthControl({ AuthorizedContext.class })
	@GraphQLDescription("Создание бэкапа базы")
	public static GraphQLQuery<RemoteObject, Boolean> backup(
			@NonNull @GraphQLName(BACKUP_NAME)
			@GraphQLDescription("Название бекапа")
			final String backupName,
			@GraphQLName(BACKUP_DIR_PATH)
			@GraphQLDescription("Директория с бекапом")
			final GOptional<String> backupDirPath
	) {
		GraphQLQuery<RemoteObject, Boolean> query = new GraphQLQuery<RemoteObject, Boolean>() {

			private RControllerBackup controllerBackup;

			@Override
			public void prepare(ResourceProvider resources) {
				controllerBackup =
						resources.getQueryRemoteController(DatabaseConsts.UUID, RControllerBackup.class);

				//TODO Ulitin V. Перевести на этот метод
				//controllerBackup = resources.getQueryRemoteController(DatabaseSubsystem.class, RControllerBackup.class);
			}

			@Override
			public Boolean execute(RemoteObject source, ContextTransactionRequest context) throws PlatformException {
				controllerBackup.createBackup(getBackupDirPath(backupDirPath), backupName);
				return true;
			}
		};
		return new GAccessQuery<>(query, CorePrivilege.GENERAL_SETTINGS, AccessOperation.WRITE);
	}

	@GraphQLField
	@GraphQLAuthControl({ AuthorizedContext.class })
	@GraphQLDescription("Восстановление базы из бэкапа")
	public static GraphQLQuery<RemoteObject, Boolean> restore(
			@NonNull @GraphQLName("new_db_path")
			@GraphQLDescription("Директория с базой данных")
			final String newDbPath,
			@NonNull @GraphQLName(BACKUP_NAME)
			@GraphQLDescription("Название бекапа")
			final String backupName,
			@GraphQLName(BACKUP_DIR_PATH)
			@GraphQLDescription("Директория с бекапом")
			final GOptional<String> backupDirPath
	) {
		GraphQLQuery<RemoteObject, Boolean> query = new GraphQLQuery<RemoteObject, Boolean>() {

			private RControllerRestore controllerRestore;

			@Override
			public void prepare(ResourceProvider resources) {
				controllerRestore = resources.getQueryRemoteController(DatabaseConsts.UUID, RControllerRestore.class);
				//TODO Ulitin V. Перевести на этот метод
				//controllerRestore = resources.getQueryRemoteController(DatabaseSubsystem.class, RControllerRestore.class);
			}

			@Override
			public Boolean execute(RemoteObject source, ContextTransactionRequest context) throws PlatformException {
				controllerRestore.restoreBackup(getBackupDirPath(backupDirPath), backupName, newDbPath);
				return true;
			}
		};
		return new GAccessQuery<>(query, CorePrivilege.GENERAL_SETTINGS, AccessOperation.WRITE);
	}

	@GraphQLField
	@GraphQLAuthControl({ AuthorizedContext.class })
	@GraphQLDescription("Копирование обезличенной базы данных")
	public static GraphQLQuery<RemoteObject, Boolean> copyDepersonalizedDatabase(
			CoreSubsystem coreSubsystem,
			@NonNull @GraphQLName(PATH)
			@GraphQLDescription("Директория для копии базы данных")
			final String destinationDirectory
	) {
		GraphQLQuery<RemoteObject, Boolean> query = new GraphQLQuery<RemoteObject, Boolean>() {

			@Override
			public void prepare(ResourceProvider resources) { }

			@Override
			public Boolean execute(RemoteObject source, ContextTransactionRequest context) throws PlatformException {
				if (!GQueryDepersonalization.stage.compareAndSet(
						DepersonalizationStage.IDLE, DepersonalizationStage.COPY)) {
					throw GeneralExceptionBuilder.buildAlreadyRunningException();
				}
				try {
					Path destinationDirectoryPath;
					try {
						destinationDirectoryPath = Paths.get(destinationDirectory);
					} catch (InvalidPathException e) {
						throw GeneralExceptionBuilder.buildInvalidValueException(PATH, destinationDirectory);
					}
					if (!destinationDirectoryPath.isAbsolute()) {
						throw GeneralExceptionBuilder.buildNotAbsolutePathException(destinationDirectory);
					}
					if (!isDirEmpty(destinationDirectoryPath)) {
						throw GeneralExceptionBuilder.buildNotEmptyDirectoryException(destinationDirectory);
					}
					Subsystems.getInstance().getQueryPool().execute(coreSubsystem, new Query<Void>() {

						private RControllerBackup rControllerBackup;
						private RControllerRestore rControllerRestore;

						@Override
						public void prepare(ResourceProvider resources) {
							rControllerBackup = resources.getQueryRemoteController(DatabaseConsts.UUID, RControllerBackup.class);
							rControllerRestore = resources.getQueryRemoteController(DatabaseConsts.UUID, RControllerRestore.class);

							//TODO Ulitin V. Перевести на этот метод
							//rControllerBackup = resources.getQueryRemoteController(DatabaseSubsystem.class, RControllerBackup.class);
							//rControllerRestore = resources.getQueryRemoteController(DatabaseSubsystem.class, RControllerRestore.class);
						}

						@Override
						public Void execute(QueryTransaction transaction) throws PlatformException {
							try {
								String backupName = "backup";
								Path backupPath = destinationDirectoryPath.resolve(backupName);
								Path dbPath = destinationDirectoryPath.resolve("database");
								rControllerBackup.createBackup(destinationDirectory, backupName);
								rControllerRestore.restoreBackup(destinationDirectory, backupName, dbPath.toString());
								try {
									FileUtils.deleteDirectoryIfExists(backupPath);
								} catch (IOException e) {
									throw GeneralExceptionBuilder.buildIOErrorException(e);
								}
								GQueryDepersonalization.stage.set(DepersonalizationStage.DEPERSONALIZATION);
								Options options = new Options(dbPath.toString());
								Subsystems.getInstance().getQueryPool().execute(coreSubsystem, new Query<Void>() {

									private Set<RControllerDepersonalization> rControllerDepersonalizations;
									private RControllerBackup rControllerBackup;
									private RControllerRestore rControllerRestore;

									@Override
									public void prepare(ResourceProvider resources) {
										rControllerDepersonalizations = resources.getQueryRemoteControllers(
												RControllerDepersonalization.class);

										rControllerBackup = resources.getQueryRemoteController(DatabaseConsts.UUID, RControllerBackup.class);
										rControllerRestore = resources.getQueryRemoteController(DatabaseConsts.UUID, RControllerRestore.class);

										//TODO Ulitin V. Перевести на этот метод
										//rControllerBackup = resources.getQueryRemoteController(DatabaseSubsystem.class, RControllerBackup.class);
										//rControllerRestore = resources.getQueryRemoteController(DatabaseSubsystem.class, RControllerRestore.class);
									}

									@Override
									public Void execute(QueryTransaction transaction) throws PlatformException {
										try {
											for (RControllerDepersonalization rControllerDepersonalization :
													rControllerDepersonalizations) {
												rControllerDepersonalization.depersonalize(options);
											}
											GQueryDepersonalization.stage.set(DepersonalizationStage.PACK);
											try {
												rControllerBackup.createBackup(destinationDirectory, backupName);
												FileUtils.deleteDirectoryIfExists(dbPath);
												rControllerRestore.restoreBackup(
														destinationDirectory, backupName, dbPath.toString());
												FileUtils.deleteDirectoryIfExists(backupPath);
											} catch (IOException e) {
												throw GeneralExceptionBuilder.buildIOErrorException(e);
											}
										} finally {
											GQueryDepersonalization.stage.set(DepersonalizationStage.IDLE);
										}
										return null;
									}
								}).exceptionally(throwable -> {
									log.error("copy_depersonalized_database mutation failed", throwable);
									return null;
								});
							} catch (Exception e) {
								GQueryDepersonalization.stage.set(DepersonalizationStage.IDLE);
								throw e;
							}
							return null;
						}
					}).exceptionally(throwable -> {
						log.error("copy_depersonalized_database mutation failed", throwable);
						return null;
					});
				} catch (Exception e) {
					GQueryDepersonalization.stage.set(DepersonalizationStage.IDLE);
					throw e;
				}

				return true;
			}

			private boolean isDirEmpty(Path directory) throws PlatformException {
				try {
					try(DirectoryStream<Path> dirStream = Files.newDirectoryStream(directory)) {
                        return !dirStream.iterator().hasNext();
                    }
				} catch (IOException e) {
					throw GeneralExceptionBuilder.buildIOErrorException(e);
				}
			}
		};
		return new GAccessQuery<>(query, CorePrivilege.GENERAL_SETTINGS, AccessOperation.WRITE);
	}

	private static String getBackupDirPath(GOptional<String> backupDirPath) {
		if (backupDirPath.isPresent()) {
			return backupDirPath.get();
		}

		DatabaseComponent databaseComponent = Platform.get().getCluster().getAnyLocalComponent(DatabaseComponent.class);
		DatabaseComponentExtensionImpl databaseExtension = (DatabaseComponentExtensionImpl)databaseComponent.getDatabaseConfigure().extension;
		return databaseExtension.config.getBackupDir().toString();
	}
}