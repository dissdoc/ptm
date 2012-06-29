class HomeController < ApplicationController
  before_filter :authenticate_user!, :only => [:profile]

  def index
    #if params[:ne_lat] && params[:ne_lng] && params[:lat] && params[:lng]
    #  max_lat = params[:ne_lat].to_f
    #  max_lng = params[:ne_lng].to_f
    #  min_lat = params[:sw_lat].to_f
    #  min_lng = params[:sw_lng].to_f
    #
    #  max_lat, min_lat = min_lat, max_lat if max_lat < min_lat
    #  max_lng, min_lng = min_lng, max_lng if max_lng < min_lng
    #
    #  @locations = Geo.where('latitude <= ? AND latitude >= ? AND longitude <= ? AND longitude >= ?', max_lat, min_lat, max_lng, min_lng)
    #  @photos = @locations.collect(&:photo)
    #else
    #  @photos = Photo.all
    #end
    #
    #respond_to do |format|
    #  format.js { render :template => 'photos/refresh.js.erb' }
    #end
    #
    #if params[:search_locations].present?
    #  @locations = Geo.near(params[:search_locations])
    #  @photos = @locations.collect(&:photo)
    #elsif params[:search_tags].present?
    #  @tag = Tag.get(params[:search_tags])
    #  @taggings = Tagging.get_by_tag(@tag)
    #  @photos = []
    #  @taggings.each do |tagging|
    #    @photos << tagging.photo
    #  end
    #elsif params[:search_dt].present?
    #  @photos = Photo.get_by_datetime(params[:search_dt].to_s)
    #else
    #  @photos = Photo.all(:joins => :share_photo, :conditions => {:share_photos => {:share => true}})
    #end

    if params[:search_tags].present?
      list_tag = params[:search_tags].split(/,\s*/)
      tags = Tag.find(:all, :conditions => ["name IN (?)", list_tag]).collect(&:id).uniq
      ids = Tagging.find(:all, :conditions => ["tag_id IN (?)", tags]).collect(&:photo)
      @photos = Photo.find(:all, :conditions => ["id IN (?)", ids]).uniq
    else
      @photos = Photo.all
    end

    render :layout => 'application_empty'
  end

  def faq
    @title_page = "FAQ"
    add_breadcrumb @title_page, ''
  end

  def contacts
    @title_page = "Contacts"
    add_breadcrumb @title_page, ''
  end

  def about
    @title_page = "About"
    add_breadcrumb @title_page, ''
  end

  def users
    if params[:query].present?
      @users = User.search(params[:query])
    else
      @users = User.free_all(current_user)
    end
  end

  def profile
    @title_page = "Profile"
    add_breadcrumb @title_page

    @invites = current_user.inviting_groups
  end

  def favorites
    @favorites = current_user.favorites
  end

  def detail
    @user = User.find(params[:id])
  end
end
