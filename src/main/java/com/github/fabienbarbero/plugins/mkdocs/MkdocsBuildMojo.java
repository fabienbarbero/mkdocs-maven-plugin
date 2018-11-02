/*
 * (C) Copyright 2018 Fabien Barbero.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser General Public License
 * (LGPL) version 2.1 which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl-2.1.html
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 */
package com.github.fabienbarbero.plugins.mkdocs;

import org.apache.maven.plugin.MojoExecution;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Component;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;
import org.apache.maven.project.MavenProjectHelper;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * @author Fabien Barbero
 */
@Mojo( name = "build", defaultPhase = LifecyclePhase.PACKAGE )
public class MkdocsBuildMojo
        extends AbstractMkdocsMojo
{

    @Parameter( defaultValue = "${project.build.directory}" )
    private File projectBuildDir;

    @Parameter( readonly = true, defaultValue = "${mojoExecution}" )
    private MojoExecution mojoExecution;

    @Parameter( readonly = true, defaultValue = "${project}" )
    private MavenProject project;

    @Component
    private MavenProjectHelper helper;

    @Override
    public void execute()
            throws MojoExecutionException, MojoFailureException
    {
        try {
            // Create output dir
            Path outputDir = projectBuildDir.toPath().resolve( "mkdocs/" + mojoExecution.getExecutionId() );
            if ( Files.notExists( outputDir ) ) {
                Files.createDirectories( outputDir );
            }

            List<String> args = new ArrayList<>();
            args.add( "mkdocs" );
            args.add( "build" );
            // Use direct config file
            args.add( "-f" );
            args.add( configFile.getCanonicalPath() );
            // Set output dir
            args.add( "-d" );
            args.add( outputDir.normalize().toString() );
            // Verbose mode
            if ( getLog().isDebugEnabled() ) {
                args.add( "-v" );
            }

            // Generate documentation
            ProcessBuilder builder = new ProcessBuilder( args );
            builder.directory( configFile.getParentFile() );
            Process proc = builder.start();
            dumpLogs( proc );
            int status = proc.waitFor();
            if ( status != 0 ) {
                throw new MojoExecutionException( "Mkdocs documentation generation has failed with status " + status );
            }
            getLog().info( "Documentation generated for " + mojoExecution.getExecutionId() );

            // Create archive
            String archiveName = String.format( "%s-%s-mkdocs-%s.zip", project.getArtifactId(), project.getVersion(), mojoExecution.getExecutionId() );
            Path archiveFile = projectBuildDir.toPath().resolve( archiveName );
            compressDirectory( outputDir, archiveFile );
            helper.attachArtifact( project, "mkdocs", mojoExecution.getExecutionId(), archiveFile.toFile() );

        } catch ( IOException | InterruptedException ex ) {
            throw new MojoExecutionException( "Error building mkdocs documentation", ex );
        }
    }

    private void compressDirectory( Path inputDir, Path outputFile )
            throws IOException
    {
        int startIndex = inputDir.normalize().toString().length();
        try ( ZipOutputStream zos = new ZipOutputStream( Files.newOutputStream( outputFile ),
                                                         StandardCharsets.UTF_8 ) ) {

            Files.walkFileTree( inputDir, new SimpleFileVisitor<Path>()
            {

                @Override
                public FileVisitResult visitFile( Path file, BasicFileAttributes attrs )
                        throws IOException
                {
                    String entryName = file.normalize().toString().substring( startIndex );
                    zos.putNextEntry( new ZipEntry( entryName ) );
                    Files.copy( file, zos );
                    zos.closeEntry();

                    return FileVisitResult.CONTINUE;
                }

            } );
            zos.flush();
        }
    }

}
