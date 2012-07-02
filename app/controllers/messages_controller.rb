class MessagesController < ApplicationController
  before_filter :authenticate_user!

  def index
    @to_me_messages = current_user.to_me
    @from_me_messages = current_user.from_me
  end

  def new
    @message = current_user.messages.new
    @to_user = params[:user_id]
  end

  def create
    @message = Message.new(:theme => params[:message][:theme],
                           :description => params[:message][:description])
    user = User.find(params[:message][:to_user].to_i)
    @message.to_user = user
    @message.from_user = current_user
    if @message.save!
      redirect_to profile_path
    else
      render :new
    end
  end

  def recommended
    link = params[:link]
    user = User.find(params[:user_id])
    @message = Message.new
    @message.theme = "Recommended..."
    @message.description = "User #{current_user.full_name} recommended #{link}"
    @message.to_user = user
    @message.from_user = user
    if @message.save!
      photo = Photo.find(params[:photo_id])
      UserMailer.shared_photo(current_user, photo).deliver
      redirect_to link
    else
      redirect_to root_path
    end
  end
end