class FriendlistsController < ApplicationController
  before_filter :authenticate_user!

  def add_friend
    @friendship = current_user.friendships.build(:friend_id => params[:friend_id])
    you = User.find(params[:friend_id])
    if @friendship.save
      UserMailer.added_friend(current_user, you).deliver
      redirect_to users_path
    else
      redirect_to root_path
    end
  end

  def remove_friend
    @friendship = current_user.friendships.find(params[:id])
    @friendship.destroy
    redirect_to users_path
  end
end