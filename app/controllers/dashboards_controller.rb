class DashboardsController < ApplicationController
  before_filter :authenticate_user!, :set_group

  def new
    @dashboard = @group.dashboards.new
    @title_page = "Create new theme"
    add_breadcrumb @group.name, @group
    add_breadcrumb 'Create new theme', ''
  end

  def create
    @dashboard = @group.dashboards.new(params[:dashboard])
    @dashboard.user_id = current_user.id
    if @dashboard.save!
      redirect_to @group
    else
      render :action => :new
    end
  end

  def show
    @dashboard = @group.dashboards.find(params[:id])
  end

  protected

  def set_group
    @group = Group.find(params[:group_id])
    redirect_to groups_path if @group.blank?
  end
end