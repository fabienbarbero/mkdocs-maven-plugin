package com.github.fabienbarbero.plugins.mkdocs;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecution;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Fabien Barbero
 */
@Mojo( name = "build", defaultPhase = LifecyclePhase.PACKAGE )
public class MkdocsBuildMojo
        extends AbstractMojo
{

    @Parameter( name = "configFile" )
    private File configFile;

    @Parameter( defaultValue = "${project.build.directory}" )
    private File projectBuildDir;

    @Parameter( readonly = true, defaultValue = "${mojoExecution}" )
    private MojoExecution mojoExecution;

//    @Parameter( readonly = true, defaultValue = "${project}" )
//    private MavenProject project;

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

            ProcessBuilder builder = new ProcessBuilder( args );
            builder.directory( configFile.getParentFile() );
            Process proc = builder.start();
            dumpLogs( proc );
            int status = proc.waitFor();
            if ( status != 0 ) {
                throw new MojoExecutionException( "Mkdocs documentation generation has failed with status " + status );
            }
            getLog().info( "Documentation generated for " + mojoExecution.getExecutionId() );

        } catch ( IOException | InterruptedException ex ) {
            throw new MojoExecutionException( "Error building mkdocs documentation", ex );
        }
    }

    private void dumpLogs( Process process )
            throws IOException
    {
        BufferedReader reader = new BufferedReader( new InputStreamReader( process.getInputStream() ) );
        String log;
        while ( ( log = reader.readLine() ) != null ) {
            getLog().info( log );
        }

        reader = new BufferedReader( new InputStreamReader( process.getErrorStream() ) );
        while ( ( log = reader.readLine() ) != null ) {
            getLog().info( log );
        }
    }

}
