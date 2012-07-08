#set :application, "set your application name here"
#set :repository,  "set your repository location here"
#
#set :scm, :subversion
# Or: `accurev`, `bzr`, `cvs`, `darcs`, `git`, `mercurial`, `perforce`, `subversion` or `none`

#role :web, "your web-server here"                          # Your HTTP server, Apache/etc
#role :app, "your app-server here"                          # This may be the same as your `Web` server
#role :db,  "your primary db-server here", :primary => true # This is where Rails migrations will run
#role :db,  "your slave db-server here"

# if you want to clean up old releases on each deploy uncomment this:
# after "deploy:restart", "deploy:cleanup"

# if you're still using the script/reaper helper you will need
# these http://github.com/rails/irs_process_scripts

# If you are using Passenger mod_rails uncomment this:
# namespace :deploy do
#   task :start do ; end
#   task :stop do ; end
#   task :restart, :roles => :app, :except => { :no_release => true } do
#     run "#{try_sudo} touch #{File.join(current_path,'tmp','restart.txt')}"
#   end
# end

set :application, "ptm"
set :repository, "https://github.com/dissdoc/ptm.git"
set :scm, :git

set :user, "www-data"
set :use_sudo, false
set :deploy_to, "/var/www/#{application}"
server "localhost", :app, :web, :db, :primary => true

set :keep_releases, 5
set :deploy_via, :remote_cache

namespace :deploy do
  task :start, :roles => :app do
    run "touch #{current_release}/tmp/restart.txt"
  end

  task :stop, :roles => :app do
    # Do nothing.
  end

  desc "Restart Application"
  task :restart, :roles => :app do
    run "touch #{current_release}/tmp/restart.txt"
  end

  desc "Regenerate css with Sass and package assets with Jammit"
  task :package_assets, :roles => :app do
    # Add `gem 'sass'` in your gemfile.rb if no task sass:update
    run "RAILS_ENV=production cd #{deploy_to}/current && rake sass:update && jammit"
  end
end

after "deploy:update", "deploy:cleanup"
before "deploy:restart", "deploy:package_assets"
