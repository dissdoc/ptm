class AssortmentsController < ApplicationController
  before_filter :authenticate_user!

  def index
    @title_page = 'Collections'
    add_breadcrumb @title_page, ''
  end

  def new
    @title_page = 'Create new collection'
    add_breadcrumb 'Collections', assortments_path
    add_breadcrumb @title_page, ''

    @assortment = current_user.assortments.new
  end

  def create
    @assortment = current_user.assortments.new(params[:assortment])
    respond_to do |format|
      if @assortment.save
        flash[:notice] = 'Form created successfully'
        format.html { redirect_to assortments_path }
      else
        format.html { render :action => :new }
      end
    end
  end

  def destroy
    @assortment = current_user.assortments.find(params[:id])
    @assortment.destroy
    redirect_to assortments_path
  end

  def show
    @assortment = current_user.assortments.find(params[:id])
    @title_page = @assortment.name
    add_breadcrumb 'My collection', assortments_path
    add_breadcrumb @title_page, ''
  end

  def add_photo_show
    @assortment = Assortment.find(params[:assortment_id])
    @title_page = @assortment.name
    add_breadcrumb 'Collection', assortments_path
    add_breadcrumb @title_page, @assortment
    add_breadcrumb 'Add photo', ''
  end

  def add_photo
    @assortment = Assortment.find(params[:assortment_id])
    @assortment.photo_assortment_joins.create!(:photo_id => params[:photo_id].to_i)
    respond_to do |format|
      format.js
    end
  end

  def remove_photo
    @assortment = Assortment.find(params[:assortment_id])
    @join = @assortment.photo_assortment_joins.where(:photo_id => params[:photo_id].to_i).first
    @join.destroy
    respond_to do |format|
      format.js { render :template => 'assortments/add_photo' }
    end
  end
end