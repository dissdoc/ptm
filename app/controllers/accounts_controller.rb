class AccountsController < ApplicationController
  def index
    @accounts = current_user.accounts if current_user
  end

  def create
    auth = request.env['omniauth.auth']
    current_user.accounts.find_or_create_by_provider_and_uid(auth['provider'], auth['uid'])
    redirect_to accounts_url
  end

  def destroy
    @accounts = current_user.accounts.find(params[:id])
    @accounts.destroy
    redirect_to accounts_url
  end
end