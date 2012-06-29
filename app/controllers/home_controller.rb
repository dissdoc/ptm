class HomeController < ApplicationController
  before_filter :authenticate_user!, :only => [:profile]

  def index
    if params[:search_tags].present?
      list_tag = params[:search_tags].split(/,\s*/)
      tags = Tag.find(:all, :conditions => ["name IN (?)", list_tag]).collect(&:id).uniq
      ids = Tagging.find(:all, :conditions => ["tag_id IN (?)", tags]).collect(&:photo)
      @photos = Photo.find(:all, :conditions => ["id IN (?)", ids]).uniq
    end

    if params[:ne_lat].present?
      photos_geo = Geo.get_all(params[:ne_lat].to_f, params[:ne_lng].to_f, params[:sw_lat].to_f, params[:sw_lng].to_f).collect(&:photo).uniq
      render :text => photos_geo
      if @photos.present? && @photos.count > 0
        @photos = @photos & photos_geo
      else
        @photos = photos_geo
      end
    end

    if @photos.blank?
      @photos = Photo.all
    end

    @photos_for_map = Photo.all

    #render :layout => 'application_empty'
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
