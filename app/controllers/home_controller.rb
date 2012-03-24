class HomeController < ApplicationController
  before_filter :authenticate_user!, :except => [:index, :users]

  def index
    if params[:search_locations].present?
      @locations = Geo.near(params[:search_locations])
      @photos = @locations.collect(&:photo)
    elsif params[:search_tags].present?
      @tag = Tag.get(params[:search_tags])
      @taggings = Tagging.get_by_tag(@tag)
      @photos = []
      @taggings.each do |tagging|
        @photos << tagging.photo
      end
    elsif params[:search_dt].present?
      @photos = Photo.get_by_datetime(params[:search_dt].to_s)
    else
      @photos = Photo.all(:joins => :share_photo, :conditions => {:share_photos => {:share => true}})
    end
  end

  def users
    if params[:query].present?
      @users = User.search(params[:query])
    else
      @users = User.all
    end
  end

  def myfriends
    @friendlists = current_user.friend lists.all
    @friendlist = current_user.friendlists.new
  end
end
