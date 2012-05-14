class AccountsController < ApplicationController
  def index
    @accounts = current_user.accounts if current_user
  end

  def create
    render :text => request.env['omniauth.auth'].to_yaml
    #omniauth = request.env['omniauth.auth']
    #account = Account.find_by_provider_and_uid(omniauth['provider'], omniauth['uid'])
    #if account
    #  sign_in_and_redirect(:user, account.user)
    #elsif current_user
    #  current_user.accounts.create!(:provider => omniauth['provider'], :uid => omniauth['uid'])
    #  redirect_to accounts_url
    #else
    #  user = User.new
    #  user.apply_omniauth(omniauth)
    #
    #  info = omniauth['info']
    #  user.current_city = info['location']
    #  user.about = info['description']
    #
    #  if user.save
    #    sign_in_and_redirect(:user, user)
    #  else
    #    session[:omniauth] = omniauth.except('extra')
    #    redirect_to new_user_registration_url
    #  end
    #end
  end

  def destroy
    @accounts = current_user.accounts.find(params[:id])
    @accounts.destroy
    redirect_to accounts_url
  end

  protected

  def handle_unverified_request
    true
  end
end