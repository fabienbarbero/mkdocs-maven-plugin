/*
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston,
 * MA 02110-1301  USA
 */
package com.github.fabienbarbero.plugins.mkdocs;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Fabien Barbero
 */
@Mojo( name = "gh-deploy", defaultPhase = LifecyclePhase.PACKAGE )
public class GithubDeployMojo
        extends AbstractMkdocsMojo {

    @Parameter( defaultValue = "Updating github page with Mkdocs documentation" )
    private String commitMessage;

    @Parameter( defaultValue = "gh-pages" )
    private String remoteBranch;

    @Override
    public void execute()
            throws MojoExecutionException {
        try {
            List<String> args = new ArrayList<>();
            args.add( "mkdocs" );
            args.add( "gh-deploy" );
            // Use direct config file
            args.add( "-f" );
            args.add( configFile.getCanonicalPath() );
            // Set output dir
            args.add( "-m" );
            args.add( commitMessage );
            // Set remote branch
            args.add( "-b" );
            args.add( remoteBranch );
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
                throw new MojoExecutionException( "Mkdocs documentation deploy has failed with status " + status );
            }

        } catch ( IOException | InterruptedException ex ) {
            throw new MojoExecutionException( "Error deploying mkdocs documentation", ex );
        }
    }
}
