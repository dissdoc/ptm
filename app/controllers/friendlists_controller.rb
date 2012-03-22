class FriendlistsController < ApplicationController
  before_filter :authenticate_user!

  def create
    @friendlist = current_user.friendlists.new(params[:friendlist])
    if @friendlist.save!
      redirect_to myfriends_path
    else
      redirect_to root_path
    end
  end
end